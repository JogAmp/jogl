#include <Cocoa/Cocoa.h>
#include <JavaVM/jni.h>
#include <dlfcn.h>
#include <pthread.h>

#define DEBUG_TRACE	1
#define TRACE(fmt, ...) \
	do { if (DEBUG_TRACE) fprintf(err, "%s:%d:%s(): " fmt "\n", __FILE__, __LINE__, __func__, __VA_ARGS__); fflush(err); } while (0)

// JNI_CreateJavaVM
typedef jint (JNICALL CREATEVM)(JavaVM **pvm, void **env, void *args);

void die(JNIEnv *env);

@interface AppDelegate : NSObject <NSApplicationDelegate>

@end

FILE *err = NULL;
JavaVM *jvm = NULL;

static const char *JNI_CREATEJAVAVM = "JNI_CreateJavaVM";
void *jvm_lib = NULL;

void *create_vm(const char *jvm_lib_path)
{
	void *sym = NULL;
	jvm_lib = dlopen(jvm_lib_path, RTLD_LAZY | RTLD_GLOBAL);
	if (jvm_lib) {
		TRACE("Found libjli.dylib%s", "");
		sym = dlsym(jvm_lib, JNI_CREATEJAVAVM);
	} else {
		TRACE("Unable to find libjli.dylib%s", "");
	}
	return sym;
}

#ifndef CLASSPATH
#define CLASSPATH ".:/Users/jogamp/projects/JogAmp/gluegen/build/gluegen-rt.jar:/Users/jogamp/projects/JogAmp/jogl/build/jar/jogl-all.jar"
#endif
#ifndef LIBPATH
#define LIBPATH "/Users/jogamp/projects/JogAmp/gluegen/build/obj:/Users/jogamp/projects/JogAmp/jogl/build/lib"
#endif

static void *launchJava(void *ptr)
{
	int k = 0;

	JNIEnv		*env = NULL;
	JNINativeMethod	nm_activity[20];
	jint		res;
	jclass		cls;
	jmethodID	mid;
	jobject		gui;
	jthrowable	ex;
	// JDK > 1.5
	JavaVMInitArgs	vm_args;

    TRACE("launchJava.1.1%s", "");
	vm_args.nOptions = 7;
	JavaVMOption options[vm_args.nOptions];
	options[0].optionString	   = "-Djava.class.path="CLASSPATH;
	options[1].optionString	   = "-Djava.library.path="LIBPATH;
	options[2].optionString	   = "-DNjogamp.debug=all";
	options[3].optionString	   = "-DNjogamp.debug.NativeLibrary=true";
	options[4].optionString	   = "-DNjogamp.debug.JNILibLoader=true";
	options[5].optionString	   = "-DNnativewindow.debug=all";
	options[6].optionString	   = "-DNjogl.debug=all";

	vm_args.version		   = JNI_VERSION_1_4;
	vm_args.options		   = options;
	vm_args.ignoreUnrecognized = JNI_TRUE;

    TRACE("launchJava.1.2%s", "");
    TRACE(".. using CLASSPATH %s", CLASSPATH);
    TRACE(".. using LIBPATH %s", LIBPATH);

	/* Create the Java VM */
	CREATEVM *CreateVM = create_vm((char *)ptr);
	TRACE("CreateVM:%lx env:%lx vm_args:%lx", (long unsigned int)CreateVM, (long unsigned int)&env, (long unsigned int)&vm_args);
	res = CreateVM(&jvm, (void**)&env, &vm_args);
	if (res < 0) {
		TRACE("Can't create Java VM%s", "");
		exit(1);
	} else {
		TRACE("VM Created%s", "");
	}

    TRACE("launchJava.1.3%s", "");
	cls = (*env)->FindClass(env, "Bug1398macOSContextOpsOnMainThread");
	ex = (*env)->ExceptionOccurred(env);
	if (ex) {
		die(env);
	}

    TRACE("launchJava.1.4%s", "");
	mid = (*env)->GetMethodID(env, cls, "<init>", "()V");
	if (mid == NULL)
		goto destroy;

    TRACE("launchJava.1.5%s", "");
	gui = (*env)->NewObject(env, cls, mid);
	TRACE("Just passed NewObject()...%s", "");


destroy:
	if ((*env)->ExceptionOccurred(env)) {
		// handle exception
        TRACE("Exception occured...%s", "");
	}

	if (err)
		fflush(err);

	if (jvm_lib) {
		dlclose(jvm_lib);
		jvm_lib = NULL;
	}

	// die(env);

    TRACE("launchJava.1.X%s", "");
	return 0;
}

void show_error_dialog(JNIEnv *env, jthrowable ex)
{
	jclass dialogClass = (*env)->FindClass(env, "javax/swing/JOptionPane");
	jmethodID showMsg = (*env)->GetStaticMethodID(env, dialogClass, "showMessageDialog", "(Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)V");

	jstring msg = (*env)->NewStringUTF(env, "\nWe be dead...\n\n");

	// extract message from exception
	jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	jclass exClass = (*env)->GetObjectClass(env, ex);
	jmethodID exGetMessage = (*env)->GetMethodID(env, exClass, "getMessage", "()Ljava/lang/String;");
	jmethodID concat = (*env)->GetMethodID(env, stringClass, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
	jstring exMsg = (*env)->CallObjectMethod(env, ex, exGetMessage);
	msg = (*env)->CallObjectMethod(env, msg, concat, exMsg); // append exception message to msg

	jstring title = (*env)->NewStringUTF(env, "Error");
	(*env)->CallStaticVoidMethod(env, dialogClass, showMsg, NULL, msg, title, (jint)0);
}

void die(JNIEnv *env)
{
	TRACE("\n*\n*\n*\ndieing...\n%s", "");

	jthrowable ex = (*env)->ExceptionOccurred(env);
	if (ex) {
		(*env)->ExceptionDescribe(env);
		show_error_dialog(env, ex);
	}

	// DestroyJavaVM hangs on Windows so just exit for now
#ifndef WIN32
	(*jvm)->DestroyJavaVM(jvm);
#else
	if (jvm_lib)
		FreeLibrary(jvm_lib);
	if (c_lib)
		FreeLibrary(c_lib);
#endif
	TRACE("VM = DEAD!%s\n", "");
	exit(0);
}

void create_jvm_thread(const char *jvm_lib_path)
{
	pthread_t vmthread;

	struct rlimit limit;
	size_t stack_size = 0;
	int rc = getrlimit(RLIMIT_STACK, &limit);

	if (rc == 0) {
		if (limit.rlim_cur != 0LL) {
			stack_size = (size_t)limit.rlim_cur;
		}
	}

    TRACE("create_jvm_thread.1.1%s", "");
	pthread_attr_t thread_attr;
	pthread_attr_init(&thread_attr);
	pthread_attr_setscope(&thread_attr, PTHREAD_SCOPE_SYSTEM);
	pthread_attr_setdetachstate(&thread_attr, PTHREAD_CREATE_DETACHED);
	if (stack_size > 0) {
		pthread_attr_setstacksize(&thread_attr, stack_size);
	}
    TRACE("create_jvm_thread.1.2%s", "");

	pthread_create(&vmthread, &thread_attr, launchJava, (void *)jvm_lib_path);
	pthread_attr_destroy(&thread_attr);
    TRACE("create_jvm_thread.1.X%s", "");
}

static AppDelegate* _appDelegate;

#if 0 

int main(int argc, const char *argv[])
{
	err = stderr;

	for (int k = 1; k < argc; k++) {
		TRACE("argv[%d]:%s", k, argv[k]);
	}
	if (argc < 2) {
		TRACE("Usage: Bug1398macOSContextOpsOnMainThread %s", "[libjli.dylib path]");
		exit(1);
	}
    TRACE("main.1%s", "");
	@autoreleasepool
	{
        TRACE("main.1.1%s", "");
		_appDelegate = [AppDelegate new];
        TRACE("main.1.2%s", "");
		[NSApplication sharedApplication];
        TRACE("main.1.3%s", "");
		[NSApp activateIgnoringOtherApps:YES];
		[NSApp setDelegate:_appDelegate];
        TRACE("main.1.5%s", "");

		create_jvm_thread(argv[1]);
        TRACE("main.1.6%s", "");

		return NSApplicationMain(argc, (const char **)argv);
        TRACE("main.1.X%s", "");
	}
}

#else

int NSApplicationMain(int argc, const char *argv[]) {
    // [NSApplication sharedApplication];
    // [NSBundle loadNibNamed:@"myMain" owner:NSApp];
    // [NSApp run];

	err = stderr;

	for (int k = 1; k < argc; k++) {
		TRACE("argv[%d]:%s", k, argv[k]);
	}
	if (argc < 2) {
		TRACE("Usage: Bug1398macOSContextOpsOnMainThread %s", "[libjli.dylib path]");
		exit(1);
	}
    TRACE("main.1%s", "");
	@autoreleasepool
	{
        TRACE("main.1.1%s", "");
		_appDelegate = [AppDelegate new];
        TRACE("main.1.2%s", "");
		[NSApplication sharedApplication];
        TRACE("main.1.3%s", "");
		[NSApp activateIgnoringOtherApps:YES];
		[NSApp setDelegate:_appDelegate];
        TRACE("main.1.5%s", "");

		create_jvm_thread(argv[1]);
        TRACE("main.1.6%s", "");

        [NSApp run];
		// return NSApplicationMain(argc, (const char **)argv);
        TRACE("main.1.X%s", "");
	}
    return 0;
}

int main(int argc, const char *argv[])
{
    return NSApplicationMain(argc, (const char **)argv);
}

#endif

@interface AppDelegate ()

@property (strong) IBOutlet NSWindow *window;

@end

@implementation AppDelegate

-(id) init
{
	self = [super init];
	NSLog(@"init");
	return self;
}

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification {

	NSLog(@"App starting...");

	//Create a new Block Operation and add it to the Operation Queue
	NSOperationQueue *operationQueue = [NSOperationQueue new];

	NSBlockOperation *startUpCompletionOperation = [NSBlockOperation blockOperationWithBlock:^{
		//The startup Object has been loaded now close the splash screen
		//This the completion block operation
		[[NSOperationQueue mainQueue] addOperationWithBlock:^{
			NSLog(@"startUpCompletionOperation main thread? ANS - %@",[NSThread isMainThread]? @"YES":@"NO");
//			launchJava((void *)"jre/lib/jli/libjli.dylib");
		}];
	}];

	NSBlockOperation *startUpOperation = [NSBlockOperation blockOperationWithBlock:^{
		// wait for everything to load and JVM to power up
		sleep(3); // wait for a bit for NewObject to complete
	}];

	[startUpCompletionOperation addDependency:startUpOperation];
	[operationQueue addOperation:startUpCompletionOperation];
	[operationQueue addOperation:startUpOperation];
}

@end
