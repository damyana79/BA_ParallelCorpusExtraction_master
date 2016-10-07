<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nl "&#x0A;">
	<!ENTITY tab "&#x09;">
]>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:v="http://ufal.mff.cuni.cz/vallex-2.0">
	<xsl:output method="text" encoding="utf-8" indent="yes" />

	<xsl:template match="v:lexeme/v:lexical_forms//v:mlemma">
		<xsl:value-of select="concat(@aspect,' ',text(),@homograph,' ',../v:commonrefl/text(),../../v:commonrefl/text())" />
		<xsl:text>&nl;</xsl:text>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:apply-templates />
	</xsl:template>
</xsl:stylesheet>
