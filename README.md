[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/G2G4DZF4D)

<img src="https://raw.githubusercontent.com/MrNavaStar/R/main/pirate.jpg" width="300" height="300">

# R
The Jolliest Java Reflection Library on the 7 Seas.

Java reflection is often a treacherous journey filled with sharks and scallywags. Let R help you navigate your way through Davy Jones Locker.

### O Captain! My Captain!
To Begin your journey, you need to wrap the object you want to plunder (reflect):
```java
R reflector = R.of(myPoorObject);
```

### Plunder The Booty
With our treasure secured, its time to dig in:
```java
// To get the value from a field (can be a private or a parent class field)
reflector.get("someField", Type.class);
// To set the value of a field (can be a private, final, or parent class field)
reflector.set("SomeField", myValue);
// To call a function (can be a private or a parent class function)
reflector.call("myFunction", ReturnType.class, param1, param2 ...);
```
