package ibn.rustum.arabistic.util;

public class CallBack {
    public static void addCallback(CallbackInterface callback){
        CallbackInterface.callbacks.add(callback);
    }
    public static void runAllCallbacks(){
        for(CallbackInterface c : CallbackInterface.callbacks){
            c.call();
        }
    }
}