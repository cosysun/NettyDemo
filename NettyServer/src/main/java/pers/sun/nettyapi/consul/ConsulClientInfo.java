package pers.sun.nettyapi.consul;

import com.orbitz.consul.model.health.ServiceHealth;

interface CallBackUpdateService {
	public void onUpdateService(ServiceHealth serviceHealth);
};

public class ConsulClientInfo {
    private String serviceId;
    private String serviceName;
    private int servicePort;
    private String consulUrl;
    private long healthCheckTime;
    private CallBackUpdateService callBack;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getConsulUrl() {
        return consulUrl;
    }

    public void setConsulUrl(String consulUrl) {
        this.consulUrl = consulUrl;
    }

    public long getHealthCheckTime() {
        return healthCheckTime;
    }

    public void setHealthCheckTime(long healthCheckTime) {
        this.healthCheckTime = healthCheckTime;
    }

    public CallBackUpdateService getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBackUpdateService callBack) {
        this.callBack = callBack;
    }
    
};