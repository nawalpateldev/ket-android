# Android App Modernization & Performance Plan

## Roadmap Overview

This plan outlines future performance optimizations and Android 15 modernization steps for the project:

### 1. Jetpack ViewModel & StateFlow
- Migrate Activity/Fragment state to `ViewModel` to survive configuration changes and screen rotations without re-fetching network data.

### 2. View Binding & Edge-to-Edge (Android 15)
- Enable `viewBinding` in `app/build.gradle` for compile-time view safety.
- Implement `enableEdgeToEdge()` and window inset handling for full Android 15 layout compatibility.

### 3. ConcatAdapter & ListAdapter DiffUtil
- Replace nested `NestedScrollView` + `RecyclerView` on Home Fragment with `ConcatAdapter`.
- Replace `notifyDataSetChanged()` with `ListAdapter` and `DiffUtil.ItemCallback` for off-thread list diffing.

### 4. Retrofit 2 & OkHttp Network Layer
- Migrate Volley `StringRequest` calls to Retrofit 2 + OkHttp for automatic HTTP/2 multiplexing and response compression.

### 5. R8 ProGuard Code Shrinking
- Enable `minifyEnabled true` and `shrinkResources true` to reduce final APK size by 30-50%.
