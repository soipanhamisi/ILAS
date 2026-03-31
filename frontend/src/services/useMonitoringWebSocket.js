/**
 * Vue 3 Composable for WebSocket monitoring
 * Handles connection lifecycle, auto-reconnect, and message parsing
 */
import { ref, onMounted, onBeforeUnmount } from 'vue'

export function useMonitoringWebSocket(adminId) {
  const isConnected = ref(false)
  const monitoringData = ref(null)
  const error = ref('')
  const lastUpdated = ref('')

  let ws = null
  let reconnectTimer = null
  let heartbeatTimer = null
  const reconnectAttempts = ref(0)
  const maxReconnectAttempts = 5
  const reconnectInterval = 3000 // 3 seconds

  /**
   * Connect to WebSocket server
   */
  const connect = () => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      console.log('[WebSocket] Already connected, skipping reconnect')
      return
    }

    try {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const wsUrl = `${protocol}//${window.location.hostname}:${window.location.port}/ws/monitoring?adminId=${adminId}`
      
      console.log('[WebSocket] Attempting to connect to:', wsUrl)
      ws = new WebSocket(wsUrl)

      ws.onopen = () => {
        isConnected.value = true
        error.value = ''
        reconnectAttempts.value = 0
        console.log('[WebSocket] ✓ Connected successfully for admin:', adminId)
        
        // Send subscription message
        sendMessage({
          type: 'subscribe',
          version: '1.0',
          payload: { adminId }
        })

        // Start heartbeat
        startHeartbeat()
      }

      ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          handleMessage(message)
        } catch (e) {
          console.error('[WebSocket] Failed to parse message:', e)
          error.value = 'Invalid message format received'
        }
      }

      ws.onerror = (event) => {
        isConnected.value = false
        console.error('[WebSocket] ✗ WebSocket error:', event)
        error.value = 'WebSocket connection error'
        attemptReconnect()
      }

      ws.onclose = (event) => {
        isConnected.value = false
        stopHeartbeat()
        console.log('[WebSocket] Connection closed. Code:', event.code, 'Reason:', event.reason)
        
        // Only attempt reconnect if it wasn't a normal closure (code 1000) and not exceeding max attempts
        if (event.code !== 1000 && reconnectAttempts.value < maxReconnectAttempts) {
          attemptReconnect()
        } else if (reconnectAttempts.value >= maxReconnectAttempts) {
          error.value = 'Unable to connect via WebSocket. Please refresh the page.'
        }
      }
    } catch (err) {
      isConnected.value = false
      error.value = 'Failed to create WebSocket connection'
      console.error('[WebSocket] Connection error:', err)
      attemptReconnect()
    }
  }

  /**
   * Handle incoming WebSocket messages
   */
  const handleMessage = (message) => {
    const { type, payload } = message

    switch (type) {
      case 'monitoring_update':
        // Update monitoring data
        monitoringData.value = payload
        lastUpdated.value = new Date().toLocaleString()
        error.value = ''
        break

      case 'acknowledge':
      case 'pong':
        // Connection or heartbeat acknowledged
        error.value = ''
        break

      case 'error':
        error.value = payload?.message || 'Server error'
        console.error('[WebSocket] Server error:', payload)
        break

      default:
        console.warn('[WebSocket] Unknown message type:', type)
    }
  }

  /**
   * Send a message via WebSocket
   */
  const sendMessage = (message) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      try {
        ws.send(JSON.stringify(message))
        console.log('[WebSocket] Message sent:', message.type)
      } catch (err) {
        console.error('[WebSocket] Failed to send message:', err)
        error.value = 'Failed to send message'
      }
    } else {
      console.warn('[WebSocket] Not connected, cannot send message. Connection state:', ws?.readyState)
    }
  }

  /**
   * Start heartbeat to keep connection alive
   */
  const startHeartbeat = () => {
    stopHeartbeat()
    console.log('[WebSocket] Starting heartbeat (30 second interval)')
    heartbeatTimer = setInterval(() => {
      if (ws && ws.readyState === WebSocket.OPEN) {
        sendMessage({
          type: 'heartbeat',
          version: '1.0',
          payload: {}
        })
      }
    }, 30000) // 30 seconds
  }

  /**
   * Stop heartbeat
   */
  const stopHeartbeat = () => {
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer)
      heartbeatTimer = null
      console.log('[WebSocket] Heartbeat stopped')
    }
  }

  /**
   * Attempt to reconnect with exponential backoff
   */
  const attemptReconnect = () => {
    if (reconnectAttempts.value >= maxReconnectAttempts) {
      error.value = 'Unable to connect via WebSocket or HTTP'
      console.error('[WebSocket] Max reconnect attempts reached')
      return
    }

    reconnectAttempts.value++
    const delayMs = reconnectInterval * reconnectAttempts.value
    
    console.log(`[WebSocket] Reconnect attempt ${reconnectAttempts.value}/${maxReconnectAttempts} in ${delayMs}ms`)
    error.value = `Reconnecting (${reconnectAttempts.value}/${maxReconnectAttempts})...`

    reconnectTimer = setTimeout(() => {
      console.log('[WebSocket] Attempting reconnect...')
      connect()
    }, delayMs)
  }

  /**
   * Disconnect WebSocket
   */
  const disconnect = () => {
    stopHeartbeat()
    
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }

    if (ws) {
      ws.close()
      ws = null
    }

    isConnected.value = false
    console.log('[WebSocket] Disconnected')
  }

  /**
   * Get fallback HTTP polling as fallback
   * Returns a function to fetch monitoring data via HTTP
   */
  const getHTTPFallback = async (adminAPI) => {
    try {
      console.log('[HTTP Fallback] Fetching monitoring data')
      const response = await adminAPI.getMonitoringSummary(adminId)
      if (response.data.success) {
        monitoringData.value = response.data.data
        lastUpdated.value = new Date().toLocaleString()
        console.log('[HTTP Fallback] ✓ Successfully fetched monitoring data')
        return true
      }
      console.error('[HTTP Fallback] Response unsuccessful:', response.data.message)
      return false
    } catch (err) {
      console.error('[HTTP Fallback] Error fetching data:', err)
      error.value = 'Unable to connect via WebSocket or HTTP'
      return false
    }
  }

  onMounted(() => {
    console.log('[WebSocket] Component mounted, connecting with adminId:', adminId)
    connect()
  })

  onBeforeUnmount(() => {
    console.log('[WebSocket] Component unmounting, disconnecting')
    disconnect()
  })

  return {
    isConnected,
    monitoringData,
    error,
    lastUpdated,
    connect,
    disconnect,
    sendMessage,
    getHTTPFallback
  }
}
