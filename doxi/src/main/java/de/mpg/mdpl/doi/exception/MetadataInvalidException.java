package de.mpg.mdpl.doi.exception;

import de.mpg.mdpl.doi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that the transfered metadata is invalid
 * @see de.mpg.mdpl.doi.exception.DoxiException
 * @author walter
 *
 */
public class MetadataInvalidException extends DoxiException {
	
	private String metadataXml;
	
	public MetadataInvalidException(String metadataXml)
	{
		super(PropertyReader.getMessage("METADATA_INVALID_EXCEPTION"));
		this.metadataXml = metadataXml;
	}
	
	public MetadataInvalidException(String metadataXml, String message)
	{
		super(message);
		this.metadataXml = metadataXml;
	}
	
	public MetadataInvalidException(String metadataXml, int statusCode)
	{
		super(statusCode);
		this.metadataXml = metadataXml;
	}
	
	public MetadataInvalidException(String metadataXml, int statusCode, String message)
	{
		super(statusCode, message);
		this.metadataXml = metadataXml;
	}
	
	public String getMetadata() {
		return this.metadataXml;
	}
}
