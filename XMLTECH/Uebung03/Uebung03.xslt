<?xml version="1.0" encoding="UTF-8"?><!-- edited with XMLSPY v2004 rel. 3 U (http://www.xmlspy.com) by kossi (NONE) --><xsl:stylesheet xmlns:ns="http://www.my-webbase.de/xml" version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>	<xsl:key name="projectREF" match="ns:project" use="@id"/>	<xsl:key name="adviceREF" match="ns:advice" use="@id"/>	<xsl:key name="personREF" match="ns:person" use="@id"/>	<xsl:template match="ns:workgroup">		<html>			<head>				<title>					<xsl:value-of select="./@name"/>				</title>			</head>			<body>				<table>					<tbody>						<tr>							<th>								<h2>									<xsl:value-of select="./@name"/>								</h2>							</th>						</tr>					</tbody>				</table>				<xsl:apply-templates/>				<p>				</p>			</body>		</html>	</xsl:template>	<xsl:template match="ns:notice">		<li>			<xsl:variable name="X">				<xsl:value-of select="@keyref"/>			</xsl:variable>			<xsl:value-of select="ns:date"/>:			<xsl:choose>				<xsl:when test="@type='Projektstart'">Start des Projekts: <a href="#{key('projectREF',@keyref)/@id}">						<xsl:value-of select="key('projectREF',@keyref)/ns:name"/>					</a>				</xsl:when>				<xsl:when test="@type='Update'">Hinweis im Netz: <a href="{key('adviceREF',@keyref)/@href}">						<xsl:value-of select="key('adviceREF',@keyref)"/>					</a>				</xsl:when>			</xsl:choose>		</li>	</xsl:template>	<xsl:template match="ns:project">		<xsl:value-of select="@name"/>:	</xsl:template>	<xsl:template match="*"/>	<xsl:template match="ns:news">		<h3>News</h3>		<ul>			<xsl:apply-templates/>		</ul>	</xsl:template>	<xsl:template match="ns:people">		<h3>People</h3>		<div>			<table>				<xsl:apply-templates/>			</table>			<br/>			<a href="{@fomerMembersURL}">Former members of this group</a>		</div>	</xsl:template>	<xsl:template match="ns:member">		<tr>			<th align="left" colspan="2">				<xsl:if test="@href">					<a href="{@href}">						<xsl:for-each select="ns:name/ns:title">							<xsl:value-of select="."/>							<xsl:text> </xsl:text>						</xsl:for-each>						<xsl:value-of select="ns:name/ns:first"/>						<xsl:text> </xsl:text>						<xsl:for-each select="ns:name/ns:middle">							<xsl:value-of select="."/>							<xsl:text> </xsl:text>						</xsl:for-each>						<xsl:value-of select="ns:name/ns:last"/>					</a>				</xsl:if>				<xsl:if test="not(@href)">					<xsl:for-each select="ns:name/ns:title">						<xsl:value-of select="."/>						<xsl:text> </xsl:text>					</xsl:for-each>					<xsl:value-of select="ns:name/ns:first"/>					<xsl:text> </xsl:text>					<xsl:for-each select="ns:name/ns:middle">						<xsl:value-of select="."/>						<xsl:text> </xsl:text>					</xsl:for-each>					<xsl:value-of select="ns:name/ns:last"/>				</xsl:if>			</th>		</tr>		<xsl:if test="ns:role">			<tr>				<td colspan="2">					<i><xsl:value-of select="ns:role"/></i>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:directdial">			<tr>				<td>Durchwahl:</td>				<td>					<xsl:value-of select="ns:directdial"/>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:email">			<tr>				<td>email:</td>				<td>					<a href="mailto:{ns:email}">						<xsl:value-of select="ns:email"/>					</a>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:assigned-projects">			<tr>				<td valign="top">Projekte:</td>				<td>					<xsl:for-each select="ns:assigned-projects/ns:projectREF">						<a href="#{key('projectREF',.)/@id}">							<xsl:value-of select="key('projectREF',.)/ns:name"/>						</a>						<br/>					</xsl:for-each>				</td>			</tr>		</xsl:if>		<tr>			<th colspan="2" valign="middle" height="5px"><hr></hr></th>		</tr>	</xsl:template>	<xsl:template match="ns:teaching">		<h3>Lehre - Teaching</h3>		<div>			<xsl:apply-templates/>		</div>	</xsl:template>	<xsl:template match="ns:courses">			</xsl:template></xsl:stylesheet>