## Pro Skier Extreme — Android Game
Pro Skier Extreme is a fast-paced tilt-controlled Android skiing game where the player dodges dynamically generated trees and rocks while racing downhill. 
The game gradually speeds up, obstacles spawn based on world distance rather than frame rate, and collisions trigger both haptic feedback and a custom crash sound. 
The app includes a menu system, settings, a live-updating top-scores screen, and a detailed “How to Play” guide, all built with Jetpack Compose.

## Figma Wireframes 
(https://www.figma.com/design/1dvo1MJVCxfFFsKQRDjOwN/Wireframe-Kit-for-Android--Community-?node-id=2002-446):


## Android & Jetpack Compose Features Used
**Jetpack Compose**
- **Canvas** for fully custom game rendering (skier sprite + obstacles)
- **drawImage()** for pixel-perfect sprite rendering
- **LaunchedEffect** for game loops, sound triggers, and haptics
- **NavHost + composable destinations** for in-app navigation
- **Scaffold, TopAppBar, Card, Slider, Switch, LazyColumn** for UI screens
- **remember / mutableStateOf** for reactive UI state
- **Dialog** for Game Over and Pause popups
- **Modifier** chains for layout, background, spacing, etc


**Android SDK Features**
- Accelerometer Tilt Control using SensorManager + SensorEventListener
- Haptic Feedback via Vibrator / VibratorManager
- Sound Effects using MediaPlayer with cleanup callbacks
- Keep Screen Awake using view.keepScreenOn tied to the game state
- Orientation Change Handling using android:configChanges="orientation|screenSize|keyboardHidden"

**State Management**
- Game logic & physics inside a GameViewModel
- Settings managed in SettingsRepository using DataStore
- High score persistence using ScoresRepository + JSON serialization

## Device Features & Minimum Requirements
To run Pro Skier Extreme, a device should have:

**Required**
- Android API 26+ (Android 8+)
- Built-in accelerometer for tilt control
- Vibration motor for haptics (optional but supported)
- Ability to play sound effects (MediaPlayer)

**Perfomance**
- A 60 FPS render loop via LaunchedEffect
- Canvas-based graphics
- Lightweight ViewModels suitable for low-end hardware

## Above and Beyond Features
- Implemented distance-based obstacle spawning.
- Created pose-changing skier animations based on accelerometer velocity.
- Added dynamic haptics and sound effects.
- Designed a fully reactive settings panel that updates gameplay sensitivity live.





  
