package RPC.protocol;


import java.io.Serializable;
import java.util.Arrays;

public class MyContent implements Serializable {
    // 请求的服务名, 一般是接口
    String serviceName;
    // 调用方法名
    String methodName;
    // 调用方法参数类型
    Class<?>[] parameterTypes;
    // 调用方法参数
    Object[] args;
    // 返回的数据（server端需要填写的内容）
    Object res;

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "MyContent{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", args=" + Arrays.toString(args) +
                ", res=" + res +
                '}';
    }
}
