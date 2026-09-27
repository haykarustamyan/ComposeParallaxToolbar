# Sample playground

`sample` is a Compose Multiplatform module with an interactive playground and the library's fixed
sample screens. Four thin hosts run it:

| Host | Run |
|---|---|
| Android (`sample-android`) | `./gradlew :sample-android:installDebug` |
| Desktop (`sample-desktop`) | `./gradlew :sample-desktop:run` |
| Web (`sample-web`) | `./gradlew :sample-web:wasmJsBrowserDistribution`, then serve `sample-web/build/dist/wasmJs/productionExecutable` |
| iOS (`iosApp`) | open `iosApp/iosApp.xcodeproj` in Xcode and run; the Kotlin framework builds through Gradle |

The configuration sheet opens on launch and reopens from the **Configure** button. Pick a screen at
the top: `playground` applies every setting below it live; the other names show the fixed sample
screens. To open a fixed screen directly: pass `--es screen <name>` to the Android activity,
a first argument to the desktop app, `?screen=<name>` on the web, or `SAMPLE_SCREEN` in the iOS
scheme's environment.

Names: `playground`, `simple`, `lazy`, `lazyPadding`, `lazyReversed`, `lazyCentered`, `lazySpacing`,
`lazyScrollControl`, `scaffold`, `aspectRatio`, `percentage`, `square`, `compact`, `ultrawide`.

A playground preset starts with a known configuration and the sheet closed, useful for demos:
`--es preset <name>` on Android, the second argument on desktop, `?preset=<name>` on the web,
`SAMPLE_PRESET` on iOS. Presets: `default`, `grid-avatar`, `enter-always-collapsed`,
`tabs-stretch`, `centered-title`, and the dark photo `showcase-basic`, `showcase-grid-avatar`,
`showcase-exit`, `showcase-exit-until`, `showcase-enter-always`, `showcase-snap`, `showcase-centered`,
`showcase-tabs` used for the README recordings. Header photos come from
[Picsum](https://picsum.photos).
