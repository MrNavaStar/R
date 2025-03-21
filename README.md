[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/G2G4DZF4D)

<img src="https://raw.githubusercontent.com/MrNavaStar/R/main/pirate.jpg" width="300" height="300">

The Jolliest Java Reflection Library on the 7 Seas.
<br></br>
Java reflection is often a treacherous journey filled with sharks and scallywags. Let R help you navigate your way through Davy Jones Locker.

### Sail, Ho!
Before leaving on your adventure, be sure to pack R aboard your ship:
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation "me.mrnavastar:r:1.0.9"
}
```

### O Captain! My Captain!
Now that were off, you need to wrap the object you want to plunder (reflect):
```java
import me.mrnavastar.r.R;

R reflector = R.of(myPoorObject);
// You can also chain .of() calls on fields inside the object you just wrapped:
R reflector = R.of(myPoorObject).of("somePoorField");
```

### Plunder The Booty!
With our treasure secured, it's time to show those landlubbers a real buccaneer:
```java
// To get the value from a field (can be a private or a parent class field)
reflector.get("someField", Type.class);
// To set the value of a field (can be a private, final, or parent class field)
reflector.set("someField", myValue);
// To call a function (can be a private or a parent class function)
reflector.call("myFunction", ReturnType.class, param1, param2 ...);

// Get type params
reflector.generics();
```

### Avast Ye!
Now that you've got your sea legs and are rolling in doubloons, be sure to loot with caution. Remember... dead men tell no tales.
