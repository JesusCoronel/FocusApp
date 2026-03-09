# FocusApp

An Android App that detects and logs distraction signals using the microphone and accelerometer.

## Architecture

The project contains three layers:

ui - Compose UI, ViewModels

domain - Models, use cases, interfaces.

data - Room, Retrofit, sensor implementations

The domain layer knows nothing about Android, Room, or Retrofit. The ViewModel never knows whether data comes from Room, the API, or a test.
Hilt connects everything together.

I decided not to implement a foreground service, It's not worth it at this moment, maybe in the future can be a better option. Also, the UI, customization and logos need more work.

## Resource Handling

Microphone (NoiseDetector): Uses AudioRecord to capture audio (mono) directly in memory. A coroutine on Dispatchers.IO reads every interval and computes the amplitude. onStop(), the hardware resource is released with AudioRecord.release().

Accelerometer (MovementDetector): We use TYPE_LINEAR_ACCELERATION (if available, because this is more efficient) and falls back to TYPE_ACCELEROMETER if unavailable. Using SENSOR_DELAY_NORMAL to minimize battery usage, there are other options like the one used in games, but that amount of precission is not needed in this case. Logic was added to calculate the movement of the phone, also the listener is unregistered immediately on stop().

## Deprioritized

API - (GET /sessions, GET /session/{id}). Without user authentication there is no way to identify whose data to fetch, so implementing these endpoints would not add real value. All session data is persisted locally in Room instead, giving the user a full in-app history across all sessions.

DetectionThresholds already centralizes all sensitivity and timing values. With more time this can be added to a Settings page, so the user can adjust noise/movement thresholds and how often detections are checked.

No test were added to the project due to the short time; unit tests for use cases, ViewModel, etc.

## Scalation

The API sync only makes sense if the server knows who the session belongs to. A login flow would associate sessions with a user account and allow access from any device.

User-configurable thresholds and intervals, as described above.

Full test suite.

There are lots of feature that can be implemented with more time, this project helped me undertand more about how to use accelerometer and microphone of the device, I would improve it in the future with more time.

Final note: The current URL added to the app is not a real API, It's just an example (https://api.focusmode.dev/).