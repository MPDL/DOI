package de.mpg.mdpl.doi.model;

import java.net.URI;

public class DOI {
	
	private String doi;
	
	private URI url;
	
	private String metadata;
	
	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}



}
