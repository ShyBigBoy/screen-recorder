/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\study\\프로젝트\\안드로이드 프로젝트\\녹화앱\\Quicklic ver 2.1\\Quicklic\\src\\quicklic\\floating\\service\\FloatingInterfaceAIDL.aidl
 */
package quicklic.floating.service;
public interface FloatingInterfaceAIDL extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements quicklic.floating.service.FloatingInterfaceAIDL
{
private static final java.lang.String DESCRIPTOR = "quicklic.floating.service.FloatingInterfaceAIDL";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an quicklic.floating.service.FloatingInterfaceAIDL interface,
 * generating a proxy if needed.
 */
public static quicklic.floating.service.FloatingInterfaceAIDL asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof quicklic.floating.service.FloatingInterfaceAIDL))) {
return ((quicklic.floating.service.FloatingInterfaceAIDL)iin);
}
return new quicklic.floating.service.FloatingInterfaceAIDL.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setDrawableQuicklic:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.setDrawableQuicklic();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setFloatingVisibility:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setFloatingVisibility(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getFloatingVisibility:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getFloatingVisibility();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setSize:
{
data.enforceInterface(DESCRIPTOR);
float _result = this.setSize();
reply.writeNoException();
reply.writeFloat(_result);
return true;
}
case TRANSACTION_getAnimation:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getAnimation();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_touched:
{
data.enforceInterface(DESCRIPTOR);
this.touched();
reply.writeNoException();
return true;
}
case TRANSACTION_doubleTouched:
{
data.enforceInterface(DESCRIPTOR);
this.doubleTouched();
reply.writeNoException();
return true;
}
case TRANSACTION_longTouched:
{
data.enforceInterface(DESCRIPTOR);
this.longTouched();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements quicklic.floating.service.FloatingInterfaceAIDL
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int setDrawableQuicklic() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setDrawableQuicklic, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setFloatingVisibility(boolean value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((value)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setFloatingVisibility, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getFloatingVisibility() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFloatingVisibility, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public float setSize() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
float _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setSize, _data, _reply, 0);
_reply.readException();
_result = _reply.readFloat();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getAnimation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAnimation, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void touched() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_touched, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void doubleTouched() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_doubleTouched, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void longTouched() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_longTouched, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setDrawableQuicklic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setFloatingVisibility = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getFloatingVisibility = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getAnimation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_touched = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_doubleTouched = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_longTouched = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public int setDrawableQuicklic() throws android.os.RemoteException;
public void setFloatingVisibility(boolean value) throws android.os.RemoteException;
public int getFloatingVisibility() throws android.os.RemoteException;
public float setSize() throws android.os.RemoteException;
public boolean getAnimation() throws android.os.RemoteException;
public void touched() throws android.os.RemoteException;
public void doubleTouched() throws android.os.RemoteException;
public void longTouched() throws android.os.RemoteException;
}
