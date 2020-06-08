package com.otc.sdk.pos.flows.sources.server.models.request;

public class DeviceIni {
	private String serialNumber;
	private boolean reloadKeys;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public boolean isReloadKeys() {
		return reloadKeys;
	}

	public void setReloadKeys(boolean reloadKeys) {
		this.reloadKeys = reloadKeys;
	}

	@Override
	public String toString() {
		return "DeviceIni{" +
				"serialNumber='" + serialNumber + '\'' +
				", reloadKeys=" + reloadKeys +
				'}';
	}
}
