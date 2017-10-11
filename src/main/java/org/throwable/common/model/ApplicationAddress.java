package org.throwable.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/11 22:32
 */
@Getter
@Setter
@AllArgsConstructor
public class ApplicationAddress {

	private String host;
	private int port;

	@Override
	public String toString() {
		StringBuilder hostAndPort = new StringBuilder();
		hostAndPort.append("http://").append(host).append(":").append(port).append("/");
		return hostAndPort.toString();
	}
}
