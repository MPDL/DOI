<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:datacite="http://datacite.org/schema/kernel-3">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="doi"/>
	<xsl:param name="auto-generated"  />
	
	<xsl:template match="@*|node()">
		<xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
	</xsl:template>
	
	<!-- Ignore namespace of identifier to allow version 3 and 4 of datacite schema -->
	<xsl:template match="*[local-name()='identifier'][@identifierType='DOI']"> 
		
		<!-- Check wether the identifier tag matches the provided DOI or if it's empty -->
		<xsl:choose>
			<xsl:when test="(text() and . = $doi) or not(text())">
				<xsl:copy>
		   			<xsl:copy-of select="@*"/>	
	            	<xsl:value-of select="$doi"/>
	        	</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">The DOI identifier tag in the provided metadata xml must either be empty or match the provided DOI from the URL.</xsl:message>
			
			</xsl:otherwise>
		
		</xsl:choose>


	</xsl:template>
	
</xsl:stylesheet>