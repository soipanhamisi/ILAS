/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        markrr: {
          cream: '#f8efdc',
          paper: '#fffefb',
          ink: '#3a1713',
          rust: '#4c1a18',
          amber: '#c36400',
          green: '#1f5a54',
          sage: '#d6decf'
        }
      },
      boxShadow: {
        markrr: '0 24px 52px rgba(75, 29, 18, 0.14)'
      }
    }
  },
  plugins: []
}

