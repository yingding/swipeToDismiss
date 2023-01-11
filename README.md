# AndroidView with horizontal drag conflicts on `SwipeToDismissBox`

This repo shows that the `AndroidView` composable contains `horizontal drag` conflict with the `swipe back` of `SwipeDismissableNavHost`.
The `edgeSwipeToDismiss` modifier, doesn't work on `AndroidView` composable to disable the `swipe` modifier of `SwipeToDismissBox` inside `SwipeDismissableNavHost` 

Dependencies:
* wear compose 1.1.0
* compose 1.3.2

Android Studio 
```
Android Studio Dolphin | 2021.3.1 Patch 1
Build #AI-213.7172.25.2113.9123335, built on September 30, 2022
Runtime version: 11.0.13+0-b1751.21-8125866 aarch64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
macOS 12.6.2
GC: G1 Young Generation, G1 Old Generation
Memory: 4096M
Cores: 10
Registry:
    external.system.auto.import.disabled=true
    ide.text.editor.with.preview.show.floating.toolbar=false
    ide.instant.shutdown=false

Non-Bundled Plugins:
    idea.plugin.protoeditor (213.6461.28)
```

* Emulator Version: 31.3.14
* AVD: Wear OS 3 - Preview ARM 64 v8a System Image API 30, Rev 11
* Hardware: Pixel Watch

## How to recreate the issue
In `GraphsScreen.kt` file, two composables contains `AndroidView` is either applied with `unswipeable` or `edgeSwipeToDismiss(swipeToDismissBoxState, 0.dp)`
```kotlin
        item {
            Text("Unswipeable")
        }
        item {
            HorizontalDraggableLineChart(
                androidViewModifier = Modifier.unswipeable(),
                chartColorInt = whiteColorInt,
                dataMap = dataMap,
                fillGradientDrawable = fillGradientDrawable!!
            )
        }
        item {
            Text("EdgeSwipeToDismiss")
        }
        item {
            HorizontalDraggableLineChart(
                androidViewModifier = Modifier.edgeSwipeToDismiss(swipeToDismissBoxState, 0.dp),
                chartColorInt = whiteColorInt,
                dataMap = dataMap,
                fillGradientDrawable = fillGradientDrawable!!
            )
        }
```
The following video shows, while the `unswipeable` hack can disable `swipe` gesture, 
but the most drag event on `AndroidMPChart` LineChart are difficult to detect, the drag feels laggy.
While the `edgeSwipeToDismiss(swipeToDismissBoxState, 0.dp)` modifier applied on `AndroidView` composable 
wraps `AndroidMPChart` LineChart, doesn't work. Drag event will be consumed by `SwipeDismissableNavHost`,
The LineChart in case `edgeSwipeToDismiss` can not be drag horizontally.

[Demo Video of Drag Event Conflict](video/dragEventConflictAndroidView.webm)

<!--
<video autoplay loop muted playsinline>
  <source src="video/dragEventConflictAndroidView.webm" type="video/webm">
</video>
-->






