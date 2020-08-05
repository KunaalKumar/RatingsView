//[ratings_view](../../index.md)/[dev.kunaal.ratings_view](../index.md)/[RatingsView](index.md)/[addArcThresholdColor](add-arc-threshold-color.md)



# addArcThresholdColor  
[androidJvm]  
Brief description  
Adds the arc color to be displayed within the given threshold. To change color, simply override the thresholdExample: Calling addThreshold(10, Color.RED), will display the color red for values 0-10 Furthermore, calling addThreshold(5, Color.BLACK), will display the color black from 0-5 and the color red from 6-10  
  


## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| color| color to be displayed within the threshold limits
| threshold| number below which color should be displayed
  
  
Content  
fun [addArcThresholdColor](add-arc-threshold-color.md)(threshold: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), color: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))  


[androidJvm]  
Brief description  
Adds given map of arc color to be displayed within the given threshold. To change color, simply override the threshold  
  


## Parameters  
  
androidJvm  
  
|  Name|  Summary| 
|---|---|
| map| map of threshold(key) and color(value) to be displayed within the threshold limits
  
  
Content  
fun [addArcThresholdColor](add-arc-threshold-color.md)(map: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>)  



