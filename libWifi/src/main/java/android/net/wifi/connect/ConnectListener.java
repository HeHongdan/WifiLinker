package android.net.wifi.connect;

public interface ConnectListener<T> {
    /**
     * 处理成功回调。
     *
     * @param response 成功内容。
     */
    void onSuccess(T response);

    /**
     * 处理失败回调。
     *
     * @param errorMessage 错误信息。
     */
    void onFailure(String errorMessage);


    /**
     * 状态改变。
     *
     * @param o 状态。
     */
    void onState(Object o);

    /**
     *
     * 状态改变。
     *
     * @param o 状态。
     * @param msg 状态描述。
     */
    void onDetailedState(Object o, String msg);



    public abstract class Simple<T> implements ConnectListener<T> {

        @Override
        public void onSuccess(T response) {

        }

        @Override
        public void onFailure(String errorMessage) {

        }

        @Override
        public void onState(Object o) {

        }

        @Override
        public void onDetailedState(Object o, String msg) {

        }
    }
}
