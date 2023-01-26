# Howto get a list of classes loaded by the JVM

## Using command line options

In JDK 8 you can use `-verbose:class` to get a trace of all classes on stdout as they are loaded:

```
$ ./corretto-8/bin/java -verbose:class -cp ~/Java HelloWorld
[Opened /share/software/Java/amazon-corretto-8.282.08.1-linux-x64/jre/lib/rt.jar]
[Loaded java.lang.Object from /share/software/Java/amazon-corretto-8.282.08.1-linux-x64/jre/lib/rt.jar]
[Loaded java.io.Serializable from /share/software/Java/amazon-corretto-8.282.08.1-linux-x64/jre/lib/rt.jar]
...
[Loaded HelloWorld from file:/home/ANT.AMAZON.COM/simonisv/Java/]
...
```

As you can see, this also prints the location from where the classes were loaded (this is not always available, e.g. generated classes,...).

The `-verbose:class` option is still available in JDK 11 and later, but is actually just a wrapper for the more general `-Xlog:class+load` command line option. On the debug level, this also prints some additional information. `-Xlog` output can also be redirected to a specific output file:

```
$ ./corretto-11/bin/java -Xlog:class+load=debug:file=classes.log -cp ~/Java HelloWorld
$ cat classes.log
[0.007s][info][class,load] opened: /share/software/Java/amazon-corretto-11.0.12.7.1-linux-x64/lib/modules
[0.049s][info][class,load] java.lang.Object source: jrt:/java.base
[0.049s][debug][class,load] klass: 0x0000000800001000 super: 0x0000000000000000 loader: [loader data: 0x00007ffff020e430 of 'bootstrap'] bytes: 1944 checksum: 87f9c409
[0.050s][info ][class,load] java.io.Serializable source: jrt:/java.base
[0.050s][debug][class,load] klass: 0x0000000800001208 super: 0x0000000800001000 loader: [loader data: 0x00007ffff020e430 of 'bootstrap'] bytes: 113 checksum: 84f7709d
...
[0,119s][info ][class,load] HelloWorld source: file:/home/ANT.AMAZON.COM/simonisv/Java/
[0,119s][debug][class,load]  klass: 0x0000000800060040 super: 0x0000000800001000 loader: [loader data: 0x00007ffff030cf50 for instance a 'jdk/internal/loader/ClassLoaders$AppClassLoader'{0x000000062ba4ff88}] bytes: 519 checksum: 26968674
...
```

Notice how the log also contains a CRC32 checksum on the debug level. This makes it possible to identify different classes with the same name (e.g. loaded by different class loaders) or redefined versions of the same class.

## Using tools

### jcmd

**JDK8:**
In jdk8, the `GC.class_stats "ClassName,ClassLoader"` command requires that the introspected JVM was started with `-XX:+UnlockDiagnosticVMOptions`.

```
$ jcmd 25172 GC.class_stats "ClassName,ClassLoader"
25172:
Index Super ClassName,ClassLoader
  1 -1 [C,NULL class_loader
  2 31 java.lang.Class,NULL class_loader
...
 31 -1 java.lang.Object,NULL class_loader
...
247 31 HelloWait,class loader 0x00007ffff013f550a 'sun/misc/Launcher$AppClassLoader'
 ...
```

**JDK11+:**
`GC.class_stats "ClassName,ClassLoader"` can be used up to and including jdk14. It’s not necessary to set  `-XX:+UnlockDiagnosticVMOptions` in the target JVM any more, but the command was removed in jdk15 and later. Apart from that there are three new `jcmd` commands which can be used to get a list of loaded classes:

```
$ jcmd 27457 VM.class_hierarchy
27457:
java.lang.Object/null
|—jdk.internal.reflect.LangReflectAccess/null (intf)
|—java.util.regex.Pattern$Node/null
| |—java.util.regex.Pattern$Start/null
...
|--HelloWait/0x00007ffff030ba40
...
```

`VM.``class_hierarchy` prints an inheritance hierarchy of all loaded classes. This view is missing the automatically generated array classes.


```
$ jcmd 27457 VM.classloaders show-classes=true
27457:
+-- <bootstrap>
      |     
      |               Classes: [Z
      |                        [C
...
      |                        sun.nio.cs.UTF_8$Decoder
      |                        (628 classes)
      |     
      |     Anonymous Classes: java.util.regex.Pattern$BmpCharPredicate$$Lambda$6/0x0000000800062040
...
      |                        java.util.regex.Pattern$$Lambda$1/0x0000000800060840
      |                        (7 anonymous classes)
      |     
      +-- "platform", jdk.internal.loader.ClassLoaders$PlatformClassLoader
            |     
            +-- "app", jdk.internal.loader.ClassLoaders$AppClassLoader
                        
                                  Classes: HelloWait
                                           (1 class)
```

`VM.classloaders show-classes=true` prints all the loaded classes ordered by the classloaders they were loaded by.

```
$ jcmd 27457 VM.metaspace   show-classes=true
...
   8: CLD 0x00007ffff030ba40: "app" instance of jdk.internal.loader.ClassLoaders$AppClassLoader
      Loaded classes:
         1:    HelloWait
      -total-: 1 class

   9: CLD 0x00007ffff020cf50: "<bootstrap>"
      Loaded classes:
         1:    sun.nio.cs.UTF_8$Decoder
...
       628:    [Z
      -total-: 628 classes
...
```

Finally, `VM.metaspace` which is actually intended for analyzing the Metaspace, can be used with the `show-loaders=true show-classes=true` options to get a list of loaded classes by “class loader data” (CLD).

### jconsole / VisualVM

`jcmd`‘s diagnostic commands are exported through the DiagnosticCommand MBean. Unfortunately, the functionality described before can’t be accessed through `jconsole`’s MBean view because it doesn’t support commands which take complex arguments (i.e. an array of Strings). Instead it is possible to use [VisualVM](https://visualvm.github.io/) together with the VisualVM-MBeans plugin as described in [this blog](http://marxsoftware.blogspot.com/2016/03/looking-at-diagnosticcommandmbean-in.html) to access this information from a graphical user interface.

## Programmatically

### Java Agent

Probably one of the most elegant (and compatible) ways to obtain a list of loaded classes (together with their class loaders) is by using a [Java agent](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html). The agent can either be attached at program startup or dynamically at runtime using the [Attach API](https://docs.oracle.com/javase/8/docs/jdk/api/attach/spec/com/sun/tools/attach/VirtualMachine.html#loadAgent-java.lang.String-) (see example below).
When attaching, the agent receives an [Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html) object which can be either used to [getAllLoadedClasses()](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html#getAllLoadedClasses--) or to register a [ClassFileTransformer](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/ClassFileTransformer.html) which will be notified of all subsequent class loading events. Both possibilities are presented below:

```
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import com.sun.tools.attach.VirtualMachine;

public class ListClassesWithAgent {

  private static Instrumentation instrumentation;

  public static void agentmain(String args, Instrumentation inst) {
    System.out.println("Loading Java Agent.");
    instrumentation = inst;
  }

  private static void loadInstrumentationAgent(String myName, byte[] buf) throws Exception {
    // Create agent jar file on the fly
    Manifest m = new Manifest();
    m.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    m.getMainAttributes().put(new Attributes.Name("Agent-Class"), myName);
    m.getMainAttributes().put(new Attributes.Name("Can-Redefine-Classes"), "true");
    File jarFile = File.createTempFile("agent", ".jar");
    jarFile.deleteOnExit();
    JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarFile), m);
    jar.putNextEntry(new JarEntry(myName.replace('.', '/') + ".class"));
    jar.write(buf);
    jar.close();
    System.out.println(jarFile);
    String self = ManagementFactory.getRuntimeMXBean().getName();
    String pid = self.substring(0, self.indexOf('@'));
    System.out.println("Our pid is = " + pid);
    VirtualMachine vm = VirtualMachine.attach(pid);
    System.out.println(jarFile.getAbsolutePath());
    vm.loadAgent(jarFile.getAbsolutePath());
  }

  private static byte[] getBytecodes(String myName) throws Exception {
    InputStream is = ListClassesWithAgent.class.getResourceAsStream(myName + ".class");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buf = new byte[4096];
    int len;
    while ((len = is.read(buf)) != -1) baos.write(buf, 0, len);
    buf = baos.toByteArray();
    System.out.println("sizeof(" + myName + ".class) == " + buf.length);
    return buf;
  }

  private static String myName = ListClassesWithAgent.class.getName();

  public static void main(String args[]) throws Exception {
    byte[] buf = getBytecodes(myName.substring(myName.lastIndexOf(".") + 1));
    loadInstrumentationAgent(myName, buf);
    long count = 0;
    for (Class c : instrumentation.getAllLoadedClasses()) {
      System.out.println(++count + " " + c + " (" + c.getClassLoader() + ")");
    }
    final long previousCount = count;
    instrumentation.addTransformer(new ClassFileTransformer() {
        long count = previousCount;
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
          System.out.println(++count + " " + className + " (" + loader + ")");
          return classfileBuffer;
        }
      });
    System.out.println("Loading javax.swing.JButton");
    // Load some Swing classes to trigger the class file transformer
    Class.forName("javax.swing.JButton");
  }
}
```

To compile and run this example program with jdk8 it is necessary to add `JAVA_HOME/lib/tools.jar` to the classpatpath. Starting with jdk9 this is not necessary anymore but instead the property  `-Djdk.attach.allowAttachSelf=true` is required when running the program in order to allow self attachment.

### JVMTI

Functionality similar to the one described for Java agents is also available for [native JVMTI agents](https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html). The [GetLoadedClasses()](https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#GetLoadedClasses) method can be used to receive an array of all loaded classes while the [ClassFileLoadHook](https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#ClassFileLoadHook) can be used to register a call-back function for class loading events:

```
//
// g++ -fPIC -shared -I $JAVA_HOME/include/ -I $JAVA_HOME/include/linux/ -o ListClasses.so ListClasses.cpp
//
#include <jvmti.h>
#include <stdio.h>
#include <string.h>

static int count = 0;

void JNICALL classFileLoadCallback(jvmtiEnv* jvmti, JNIEnv* jni,
                                   jclass class_being_redefined,
                                   jobject loader,
                                   const char* name,
                                   jobject protection_domain,
                                   jint class_data_len,
                                   const unsigned char* class_data,
                                   jint* new_class_data_len,
                                   unsigned char** new_class_data) {
  fprintf(stdout, "%d %s (%p)\n", ++count, name, loader);
}

static void printClass(jvmtiEnv* jvmti, jclass klass) {
  char *className;
  if (jvmti->GetClassSignature(klass, &className, NULL) != JVMTI_ERROR_NONE) {
    fprintf(stdout, "%s of %p\n", "Can't get class signature", klass);
    return;
  }
  className[strlen(className) - 1] = '\0'; // Strip trailing ';'
  fprintf(stdout, "%d %s\n", ++count, (className[0] == 'L') ? className + 1 : className);
  fflush (NULL);
  jvmti->Deallocate((unsigned char*) className);
}

static jint registerCallbacks(jvmtiEnv* jvmti) {
  jvmtiEventCallbacks callbacks;
  memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));
  callbacks.ClassFileLoadHook = classFileLoadCallback;
  if (jvmti->SetEventCallbacks(&callbacks, sizeof(jvmtiEventCallbacks)) != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't set event callbacks!\n");
    return JNI_ERR;
  }
  if (jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL) != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't enable JVMTI_EVENT_CLASS_FILE_LOAD_HOOK!\n");
    return JNI_ERR;
  }
  return JNI_OK;
}

extern "C"
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* jvm, char* options, void* reserved) {
  jvmtiEnv* jvmti = NULL;
  jvmtiCapabilities capa;
  jvmtiError error;
  jint result = jvm->GetEnv((void**) &jvmti, JVMTI_VERSION_1_1);
  if (result != JNI_OK) {
    fprintf(stderr, "Can't access JVMTI!\n");
    return JNI_ERR;
  }
  return registerCallbacks(jvmti);
}

extern "C"
JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* jvm, char* options, void* reserved) {
  jvmtiEnv* jvmti = NULL;
  jvmtiCapabilities capa;
  jvmtiError error;
  jint result = jvm->GetEnv((void**) &jvmti, JVMTI_VERSION_1_1);
  if (result != JNI_OK) {
    fprintf(stderr, "Can't access JVMTI!\n");
    return JNI_ERR;
  }

  jint nClasses;
  jclass *classes;
  if (jvmti->GetLoadedClasses(&nClasses, &classes)  != JVMTI_ERROR_NONE) {
    fprintf(stderr, "Can't get loaded classes!\n");
    return JNI_ERR;
  }
  for (jint i = 0; i < nClasses; i++) {
    printClass(jvmti, classes[i]);
  }

  return registerCallbacks(jvmti);
}
```

If this agent will be attached at JVM startup with the `-agentpath:ListClasses.so` option the `Agent_OnLoad()` function will be called which registers a `ClassFileLoadHook` which in turn prints every class which gets loaded:

```
java -agentpath:/tmp/ListClasses.so -cp ~/Java HelloWorld
1 java/lang/Object ((nil))
2 java/lang/String ((nil))
3 java/io/Serializable ((nil))
...
432 HelloWorld (0x7ffff008ac98)
...
```

If the same native agent will be attached dynamically at runtime, the `Agent_OnAttach` function will be called which will first print all the classes which have been loaded by the JVM before it also registers a `ClassFileLoadHook` which will print all the class which get loaded after the agent was loaded:

```
import java.lang.management.ManagementFactory;
import com.sun.tools.attach.VirtualMachine;

public class ListClassesJVMTI {

  private static void loadJvmtiAgent(String jvmtiAgent) throws Exception {
    String self = ManagementFactory.getRuntimeMXBean().getName();
    String pid = self.substring(0, self.indexOf('@'));
    System.out.println("Our pid is = " + pid);
    VirtualMachine vm = VirtualMachine.attach(pid);
    vm.loadAgentPath(jvmtiAgent);
  }

  public static void main(String args[]) throws Exception {
    if (args.length == 0) {
      System.out.println("Error: require the JVMTI agent as first argument!");
      System.exit(-1);
    }
    loadJvmtiAgent(args[0]);
    System.out.println("Loading javax.swing.JButton");
    // Load some Swing classes to trigger the class file transformer
    Class.forName("javax.swing.JButton");
  }
}
```

To compile and run this example program with jdk8 it is necessary to add `JAVA_HOME/lib/tools.jar` to the classpatpath. Starting with jdk9 this is not necessary anymore but instead the property  `-Djdk.attach.allowAttachSelf=true` is required when running the program in order to allow self attachment.

### JMX

Finally, it is also possible to programmatically access the various jcmd commands described before through the [JMX interface](https://docs.oracle.com/javase/8/docs/technotes/guides/jmx/index.html):

```
import java.lang.management.ManagementFactory;
import java.util.Scanner;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ListClassesJMX {

  private static MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();

  public static void main(String[] args) throws Exception {
    ObjectName diagCmd = new ObjectName("com.sun.management:type=DiagnosticCommand");

    System.out.println("==== GC.class_stats \"ClassName,ClassLoader\" ====");
    String classes = (String)mbserver.invoke(diagCmd , "gcClassStats",
                                             new Object[] { new String[] { "ClassName,ClassLoader" } },
                                             new String[] { String[].class.getName()} );
    Scanner s = new Scanner(classes);
    while (s.hasNextLine()) {
      String cl = s.nextLine();
      System.out.println(cl);
    }

    System.out.println("==== VM.class_hierarchy ====");
    classes = (String)mbserver.invoke(diagCmd , "vmClassHierarchy",
                                      new Object[] { null },
                                      new String[] { String[].class.getName()} );
    s = new Scanner(classes);
    while (s.hasNextLine()) {
      String cl = s.nextLine();
      System.out.println(cl);
    }

    System.out.println("==== VM.classloaders show-classes=true ====");
    classes = (String)mbserver.invoke(diagCmd , "vmClassloaders",
                                      new Object[] { new String[] { "show-classes=true" } },
                                      new String[] { String[].class.getName()} );
    s = new Scanner(classes);
    while (s.hasNextLine()) {
      String cl = s.nextLine();
      System.out.println(cl);
    }

    System.out.println("==== VM.metaspace show-loaders=true show-classes=true ====");
    classes = (String)mbserver.invoke(diagCmd , "vmMetaspace",
                                      new Object[] { new String[] { "show-loaders=true",
                                                                    "show-classes=true" } },
                                      new String[] { String[].class.getName()} );
    s = new Scanner(classes);
    while (s.hasNextLine()) {
      String cl = s.nextLine();
      System.out.println(cl);
    }
  }
}
```

### Hacky (not recommended, just mentioned for completeness)

In OpenJDK, every `ClassLoader` object has a private field `classes` (of type `Vector<Class<?>>` in jdk8 and of type `ArrayList<Class<?>>` in jdk9 and later) which contains a list of all the classes it has loaded. Using reflection, this field can be accessed to list all the classes loaded by the corresponding class loader:

```
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractList;

public class ListClasses {

  public static Field getClassesField(Class cl) throws Exception {
    Method m = Class.forName("java.lang.Class").getDeclaredMethod("getDeclaredFields0",
                                                                  new Class[] {boolean.class});
    m.setAccessible(true);
    Field[] fields = (Field[])m.invoke(cl, new Object[] { false });
    for (Field f : fields) {
      if ("classes".equals(f.getName())) {
        f.setAccessible(true);
        return f;
      }
    }
    return null;
  }

  public static void main(String args[]) throws Exception {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    Field classes_f = getClassesField(Class.forName("java.lang.ClassLoader"));
    AbstractList<Class<?>> classes = (AbstractList<Class<?>>)classes_f.get(cl);
    for (int i = 0; i < classes.size(); i++) {
      System.out.println((i+1) + " " + classes.get(i).getName());
    }
```

This approach is hacky because it depends on unspecified implementation details. Also, upcoming jdks might make it impossible to access the private `classes` field (jdk9+ already requires the usage of
`—add-opens=java.base/java.lang=<target-module>` where `<target-module>` can be `ALL-UNNAMED` if the field will be accessed from a class outside of a module).
Finally, a complete list of application class loaders is required in order to get all loaded application classes and it’s not possible to query the classes loaded by the bootstrap class loader with this hack because the bootstrap class loader is builtin to the JVM and represented by the `null` value in Java.
