package org.throwable.common.support;

import org.throwable.common.model.ApplicationAddress;

import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/11 22:50
 */
public interface ApplicationAddressSelector {

	ApplicationAddress choose(String applicationName);

	List<ApplicationAddress> select(String applicationName);
}
