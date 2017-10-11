package org.throwable.common.support;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.client.ClientFactory;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Application;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.common.model.ApplicationAddress;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/11 22:51
 */
public class DefaultApplicationAddressSelector implements ApplicationAddressSelector {

	private static final String CONFIGURATION_FILE_NAME = "configuration.properties";
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultApplicationAddressSelector.class);
	private static final RoundRobinRule DEFAULT_RULE = new RoundRobinRule();

	static {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Start to init ApplicationAddressSelector...");
		}
		try {
			ConfigurationManager.loadPropertiesFromResources(CONFIGURATION_FILE_NAME);
		} catch (Exception e) {
			LOGGER.error("Init ribbon configuration failed!", e);
			throw new IllegalStateException(e);
		}
		DiscoveryManager.getInstance().initComponent(new MyDataCenterInstanceConfig(), new DefaultEurekaClientConfig());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("End to init ApplicationAddressSelector...");
		}
	}

	@Override
	public ApplicationAddress choose(String applicationName) {
		// ClientFactory.getNamedLoadBalancer会缓存结果, 所以不用担心它每次都会向eureka发起查询
		DynamicServerListLoadBalancer lb = (DynamicServerListLoadBalancer) ClientFactory.getNamedLoadBalancer(applicationName);
		Server server = DEFAULT_RULE.choose(lb, null);
		if (null == server) {
			LOGGER.warn("服务{}没有可用地址", applicationName);
			return null;
		}
		return new ApplicationAddress(server.getHost(), server.getPort());
	}

	@Override
	public List<ApplicationAddress> select(String applicationName) {
		DynamicServerListLoadBalancer lb = (DynamicServerListLoadBalancer) ClientFactory.getNamedLoadBalancer(applicationName);
		List<Server> reachableServers = lb.getReachableServers();
		if (null == reachableServers || reachableServers.isEmpty()) {
			LOGGER.warn("服务{}没有可用地址", applicationName);
			return Collections.emptyList();
		}
		return reachableServers.parallelStream()
				.map(server -> new ApplicationAddress(server.getHost(), server.getPort()))
				.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		ApplicationAddressSelector addressSelector = new DefaultApplicationAddressSelector();
		ApplicationAddress choose = addressSelector.choose("order-server");
		System.out.println(choose);
		Application application = DiscoveryManager.getInstance().getDiscoveryClient().getApplication("order-server");
		System.out.println(application);
	}
}
