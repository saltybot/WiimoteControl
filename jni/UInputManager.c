#include <jni.h>

#include <fcntl.h>
//#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include <linux/input.h>
#include <linux/uinput.h>
#include <sys/ioctl.h>

static const char* DEV_NAME = "puterpants pointer";

static int fd;
static struct uinput_user_dev dev;
static struct input_event ev;

void sendEvent(uint16_t type, uint16_t code, int32_t value)
{
  ev.type = type;
  ev.code = code;
  ev.value = value;
  write(fd, &ev, sizeof(ev));
}

JNIEXPORT void JNICALL Java_com_tripletheta_wiimote_UInputManager_destroy(
  JNIEnv* env, jobject obj)
{
  ioctl(fd, UI_DEV_DESTROY);
  close(fd);
}

JNIEXPORT jint JNICALL Java_com_tripletheta_wiimote_UInputManager_init(
  JNIEnv* env, jobject obj)
{
  fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
  if (fd < 0) {
    return -1;
  }

  ioctl(fd, UI_SET_EVBIT, EV_KEY);
  ioctl(fd, UI_SET_EVBIT, EV_ABS);
  ioctl(fd, UI_SET_EVBIT, EV_REL);

  for (int i=0; i<KEY_MAX; i++) {
    ioctl(fd, UI_SET_KEYBIT, i);
  }

  for (int i=0; i<ABS_MAX; i++) {
    ioctl(fd, UI_SET_ABSBIT, i);
  }

  for (int i=0; i<REL_MAX; i++) {
    ioctl(fd, UI_SET_RELBIT, i);
  }

  memset(&dev, 0, sizeof(dev));
  strncpy(dev.name, DEV_NAME, UINPUT_MAX_NAME_SIZE);
  dev.id.bustype = BUS_USB;
  dev.id.version = 1;
  /**
    * wonky alert!
    */
  dev.absmin[ABS_X] = 0;
  dev.absmax[ABS_X] = 1279;
  dev.absmin[ABS_Y] = 0;
  dev.absmax[ABS_Y] = 719;

  int rc;
  rc = write(fd, &dev, sizeof(dev));
  if (rc < 0) {
    Java_com_tripletheta_wiimote_UInputManager_destroy(env, obj);
    return -1;
  }

  rc = ioctl(fd, UI_DEV_CREATE);
  if (rc < 0) {
    Java_com_tripletheta_wiimote_UInputManager_destroy(env, obj);
    return -1;
  }

  return 0;
}

JNIEXPORT void JNICALL Java_com_tripletheta_wiimote_UInputManager_movePointerAbsolute(
  JNIEnv* env, jobject obj, jint x, jint y)
{
  memset(&ev, 0, sizeof(ev));
  sendEvent(EV_ABS, ABS_X, x);
  sendEvent(EV_ABS, ABS_Y, y);
  sendEvent(EV_SYN, SYN_REPORT, 0);
}

JNIEXPORT void JNICALL Java_com_tripletheta_wiimote_UInputManager_movePointerRelative(
  JNIEnv* env, jobject obj, jint x, jint y)
{
  memset(&ev, 0, sizeof(ev));
  sendEvent(EV_REL, REL_X, x);
  sendEvent(EV_REL, REL_Y, y);
  sendEvent(EV_SYN, SYN_REPORT, 0);
}

