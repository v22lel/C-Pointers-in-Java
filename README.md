This is an example of a simple int pointer:
```java
Pointer<Integer> ptr = calloc(1, sizeof(int.class));
ptr.set(1);
```
They same thing can be achieved like this:
```java
Pointer<Integer> ptr = ref(1);
```
To read the value of a pointer, simply dereference it by using the `deref()` method.
```java
System.out.println(ptr.deref());
```
Of cource, you can also have a `Pointer<Pointer<Integer>>` ;)

To create an Array, pass an int greater than 1 as the first argument for the `calloc()` or the `malloc()` method.
Use the `at()` method to access a slot. To read a slot, increment the pointer by the index you want to access.
```java
//Create the array
Pointer<Character> x = malloc(7, sizeof(char.class));

//Access the slots 1 by 1
x.at(0, 'p');
x.at(1, 'o');
x.at(2, 'i');
x.at(3, 'n');
x.at(4, 't');
x.at(5, 'e');
x.at(6, 'r');

//convert it to String
printf("%s", x.as_str(7));

//Read a slot
char c = x.incr(3).deref() //c -> n
```

The funny thing about this little toy is, that every value is stored in a literal byte array in the `Memory` class. So its no simple `ArrayList`, but a "real" memory manager.
A little detail which I think is funny, is that the addresses you can receive from a pointer have cool looking values like -5463669687428276857 that are actually working.

Anyways, have fun playing with this mess ;D
