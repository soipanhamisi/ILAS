# UI Images Directory

Store UI image assets for the frontend here.

## Suggested structure

- `avatars/` for user/profile placeholders
- `course-covers/` for course thumbnails
- `dashboard/` for decorative or chart background images

## Naming convention

Use kebab-case names, for example:

- `student-avatar-01.png`
- `course-design-systems.jpg`
- `dashboard-hero.webp`

## Usage in Vue components

```vue
<script setup>
import courseCover from '../assets/ui-images/course-covers/course-design-systems.jpg'
</script>

<template>
  <img :src="courseCover" alt="Course cover" />
</template>
```

