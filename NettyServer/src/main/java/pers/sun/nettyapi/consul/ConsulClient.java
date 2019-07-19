package pers.sun.nettyapi.consul;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.ServiceHealth;

import io.netty.util.internal.logging.Log4JLoggerFactory;

public class ConsulClient {
    private Consul client = null;
    private ConsulClientInfo clientInfo = null;
    private ServiceHealthCache svHealth = null;
    private static class ServerMgrInstance{
        private static final ConsulClient instance=new ConsulClient();
    }
        
    private ConsulClient(){
    }
        
    public static ConsulClient getInstance(){
        return ServerMgrInstance.instance;
    }

    public boolean Init(ConsulClientInfo info)  {
        this.clientInfo = info;
        this.client = Consul.builder().withUrl(info.getConsulUrl()).build(); 
        return this.registSelf(info.getServiceId(), info.getServiceName(), info.getServicePort(), info.getHealthCheckTime());
    }

    private boolean registSelf(String serviceId, String serviceName, int servicePort, long healthCheckTime) {
        if (this.client != null) return false;
        AgentClient agentClient = this.client.agentClient();
        Registration service = ImmutableRegistration.builder()
                .id(serviceId)
                .name(serviceName)
                .port(servicePort)
                .check(Registration.RegCheck.ttl(healthCheckTime)) // registers with a TTL of 3 seconds
                .tags(Collections.singletonList("tag1"))
                .meta(Collections.singletonMap("version", "1.0"))
                .build();
        agentClient.register(service);
        return true;
    }

    public List<ServiceHealth> getServiceList(String serviceName) {
        ConsulResponse<List<ServiceHealth>> response = this.client.healthClient().getHealthyServiceInstances(serviceName);
        return response.getResponse();
    }

    // public void startListenUpdateService() {
    //     HealthClient healthClient = client.healthClient();
    //     this.svHealth = ServiceHealthCache.newCache(healthClient, this.clientInfo.getServiceName());
    //     this.svHealth.addListener((Map<ServiceHealthKey, ServiceHealth> newValues) -> {
    //         ServiceHealth serviceHealth = newValues.get(this.clientInfo.getServiceId());
    //         if(serviceHealth != null) {
    //             this.clientInfo.getCallBack().onUpdateService(serviceHealth);
    //         }
    //         System.out.println(serviceHealth);
    //     });
    //     this.svHealth.start();
    // }
}