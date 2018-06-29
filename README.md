# Unit

## Usage and Installation
Download the [**latest release**](https://github.com/ecen/unit/releases/latest) jar files and add them to your project structure or classpath. To have your IDE display the javadoc you need to attach the javadoc jar together with the main jar. (If you're unsure of how to do this, consult the manual of your IDE on how to add a library to your project.)

## License
This project is distributed under the [GNU LGPLv3](https://choosealicense.com/licenses/lgpl-3.0/) license. This means that any improvements or modifications must be shared under the same license, but larger works using this library may use it without such restrictions. Read the LICENSE file for the full statement.

## Background
This is a small Java library that makes it easier to work with variables modeling physical quantities. It does this by attaching physical quantities, units, to variables in order to perform dimensional analysis. It will stop you from doing undefined operations like `2 m + 2 kg`, but allow legal ones like `2 m * 2 kg = 4 kg*m`. It is also easy to define entirely custom units.

## Basic Usage
There are mainly two classes that you will have to use. `U` defines a unit and `UV` defines a unit with a value. Both classes support similar arithmetic operations like `add`, `mul`, `div` and `pow`.

Here are two examples of these operations can be chained on existing units to create new ones. The pre-defined units `U.KG`, `U.M` and `U.S` represents kilograms, meters and seconds, respectively.
```java
U newton = U.KG.mul(U.M).div(U.S.pow(2));
System.out.println(newton); // Prints "kg * m / s^2".

UV f = new UV(5, newton).add(new UV(3, newton));
System.out.println(f); // Prints "8.00 kg * m / s^2".
```

It is also possible define new units from scratch. If we wanted to define Newton as above, but give it a proper name, we can do like this:
```java
U newton = new U(U.KG.mul(U.M).div(U.S.pow(2)), 1, "N", "Newton");
UV f = new UV(5, newton).add(new UV(3, newton));

System.out.println(f); // Prints "8.00 N".
System.out.println(f.convert(U.KG.mul(U.M).div(U.S.pow(2)))); // Prints "8.00 kg * m / s^2".
```

This defines a unit called Newton with the abbreviation N. The number 1 signifies the length compared to the reference unit. If we for wanted to define kilo-Newton, we would write give it a length of 1000 compared to Newton.

## Creating Entirely New Units

A unit may be defined from scratch using the `Quantity` class. This class represents a raw physical quantity and a power. For instance, DISTANCE with power 2 would represent an area. The raw physical quantity is as of writing this accessed through the 'Base' enum, but it should in the future be possible to define your own as well.

As an example, this is how Meter is defined internally:
```java
U M = new U(1, "m", "meter", new Quantity(Base.DISTANCE, 1));
```

One thing to note is that the first 1 here is the absolute length that will be used in the internal representation of this unit and could essentially be any number. The length given to units defined on meter is in relative terms to this number.
