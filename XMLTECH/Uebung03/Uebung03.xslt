<?xml version="1.0" encoding="UTF-16"?><!-- edited with XMLSPY v2004 rel. 3 U (http://www.xmlspy.com) by kossi (NONE) --><xsl:stylesheet xmlns:ns="http://www.my-webbase.de/xml" version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>	<xsl:key name="projectREF" match="ns:project" use="@id"/>	<xsl:key name="adviceREF" match="ns:advice" use="@id"/>	<xsl:key name="personREF" match="ns:person" use="@id"/>	<xsl:template match="ns:workgroup">		<html>			<head>				<title>					<xsl:value-of select="./@name"/>				</title>			</head>			<body>				<table>					<tbody>						<tr>							<th>								<h2>									<xsl:value-of select="./@name"/>								</h2>							</th>						</tr>					</tbody>				</table>				<xsl:apply-templates/>				<p>				</p>			</body>		</html>	</xsl:template>	<xsl:template match="ns:notice">		<li>			<xsl:value-of select="ns:date"/>:			<xsl:choose>				<xsl:when test="@type='Projektstart'">Start des Projekts: <a href="#{key('projectREF',@keyref)/@id}">						<xsl:value-of select="key('projectREF',@keyref)/ns:name"/>					</a>				</xsl:when>				<xsl:when test="@type='Update'">Hinweis im Netz: <a href="{key('adviceREF',@keyref)/@href}">						<xsl:value-of select="key('adviceREF',@keyref)"/>					</a>				</xsl:when>			</xsl:choose>		</li>	</xsl:template>	<xsl:template match="ns:project">		<xsl:value-of select="@name"/>:	</xsl:template>	<xsl:template match="*"/>	<xsl:template match="ns:news">		<h3>News</h3>		<ul>			<xsl:apply-templates/>		</ul>	</xsl:template>	<xsl:template match="ns:people">		<h3>People</h3>		<div>			<table>				<xsl:apply-templates/>			</table>			<br/>			<a href="{@fomerMembersURL}">Former members of this group</a>		</div>	</xsl:template>	<xsl:template match="ns:member">		<tr>			<th align="left" colspan="2">				<xsl:if test="@href">					<a href="{@href}">						<xsl:for-each select="ns:name/ns:title">							<xsl:value-of select="."/>							<xsl:text> </xsl:text>						</xsl:for-each>						<xsl:value-of select="ns:name/ns:first"/>						<xsl:text> </xsl:text>						<xsl:for-each select="ns:name/ns:middle">							<xsl:value-of select="."/>							<xsl:text> </xsl:text>						</xsl:for-each>						<xsl:value-of select="ns:name/ns:last"/>					</a>				</xsl:if>				<xsl:if test="not(@href)">					<xsl:for-each select="ns:name/ns:title">						<xsl:value-of select="."/>						<xsl:text> </xsl:text>					</xsl:for-each>					<xsl:value-of select="ns:name/ns:first"/>					<xsl:text> </xsl:text>					<xsl:for-each select="ns:name/ns:middle">						<xsl:value-of select="."/>						<xsl:text> </xsl:text>					</xsl:for-each>					<xsl:value-of select="ns:name/ns:last"/>				</xsl:if>			</th>		</tr>		<xsl:if test="ns:role">			<tr>				<td colspan="2">					<i>						<xsl:value-of select="ns:role"/>					</i>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:directdial">			<tr>				<td>Durchwahl:</td>				<td>					<xsl:value-of select="ns:directdial"/>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:email">			<tr>				<td>email:</td>				<td>					<a href="mailto:{ns:email}">						<xsl:value-of select="ns:email"/>					</a>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:assigned-projects">			<tr>				<td valign="top">Projekte:</td>				<td>					<xsl:for-each select="ns:assigned-projects/ns:projectREF">						<a href="#{key('projectREF',.)/@id}">							<xsl:value-of select="key('projectREF',.)/ns:name"/>						</a>						<br/>					</xsl:for-each>				</td>			</tr>		</xsl:if>		<tr>			<th colspan="2" valign="middle" height="5px">				<hr/>			</th>		</tr>	</xsl:template>	<xsl:template match="ns:teaching">		<h3>Lehre - Teaching</h3>		<xsl:apply-templates/>	</xsl:template>	<xsl:template match="ns:authors">		<h3>Authors</h3>		<div>			<table>				<xsl:apply-templates/>			</table>		</div>	</xsl:template>	<xsl:template match="ns:person">		<tr>			<th align="left" colspan="2">				<xsl:if test="@href">					<a href="{@href}">						<xsl:for-each select="ns:name/ns:title">							<xsl:value-of select="."/>							<xsl:text> </xsl:text>						</xsl:for-each>						<xsl:value-of select="ns:name/ns:first"/>						<xsl:text> </xsl:text>						<xsl:for-each select="ns:name/ns:middle">							<xsl:value-of select="."/>							<xsl:text> </xsl:text>						</xsl:for-each>						<xsl:value-of select="ns:name/ns:last"/>					</a>				</xsl:if>				<xsl:if test="not(@href)">					<xsl:for-each select="ns:name/ns:title">						<xsl:value-of select="."/>						<xsl:text> </xsl:text>					</xsl:for-each>					<xsl:value-of select="ns:name/ns:first"/>					<xsl:text> </xsl:text>					<xsl:for-each select="ns:name/ns:middle">						<xsl:value-of select="."/>						<xsl:text> </xsl:text>					</xsl:for-each>					<xsl:value-of select="ns:name/ns:last"/>				</xsl:if>			</th>		</tr>		<xsl:if test="ns:phone">			<tr>				<td>Phone:</td>				<td>					<xsl:value-of select="ns:phone"/>				</td>			</tr>		</xsl:if>		<xsl:if test="ns:email">			<tr>				<td>email:</td>				<td>					<a href="mailto:{ns:email}">						<xsl:value-of select="ns:email"/>					</a>				</td>			</tr>		</xsl:if>		<tr>			<th colspan="2" valign="middle" height="5px">				<hr/>			</th>		</tr>	</xsl:template>	<xsl:template match="ns:info">		<h3>Info</h3>		<div>			<table>				<tr>					<th>						<xsl:if test="ns:logo">							<img src="{ns:logo}" alt=""/>						</xsl:if> Homepages</th>				</tr>				<tr>					<td>						<xsl:if test="not(ns:university/@href)">							<xsl:value-of select="ns:university"/>						</xsl:if>						<xsl:if test="ns:university/@href">							<a href="{ns:university/@href}">								<xsl:value-of select="ns:university"/>							</a>						</xsl:if>					</td>				</tr>				<tr>					<td>						<xsl:if test="not(ns:department/@href)">							<xsl:value-of select="ns:department"/>						</xsl:if>						<xsl:if test="ns:department/@href">							<a href="{ns:department/@href}">								<xsl:value-of select="ns:department"/>							</a>						</xsl:if>					</td>				</tr>				<tr>					<td>						<xsl:if test="not(ns:institute/@href)">							<xsl:value-of select="ns:institute"/>						</xsl:if>						<xsl:if test="ns:institute/@href">							<a href="{ns:institute/@href}">								<xsl:value-of select="ns:institute"/>							</a>						</xsl:if>					</td>				</tr>				<tr>					<th>						<hr/>					</th>				</tr>				<tr>					<th>Adresse</th>				</tr>				<tr>					<td>						<xsl:value-of select="ns:address/ns:name"/>						<br/>						<xsl:value-of select="ns:address/ns:street"/>						<br/>						<xsl:value-of select="ns:address/ns:zipcode"/>						<br/>						<xsl:value-of select="ns:address/ns:city"/>						<br/>						<xsl:value-of select="ns:address/ns:country"/>						<br/>					</td>				</tr>				<tr>					<th>						<hr/>					</th>				</tr>				<tr>					<th>Telefon</th>				</tr>				<tr>					<td>						Vorwahl: <xsl:value-of select="ns:telephone/ns:prefix"/>						<br/>						Standarddurchwahl: <xsl:value-of select="ns:telephone/ns:default-directdial"/>						<br/>						<xsl:if test="ns:telephone/ns:fax-directdial">Faxdurchwahl: <xsl:value-of select="ns:telephone/ns:fax-directdial"/>							<br/>						</xsl:if>					</td>				</tr>				<tr>					<th>						<hr/>					</th>				</tr>				<tr>					<th>Office</th>				</tr>				<tr>					<td>						<xsl:value-of select="ns:public-office/ns:address/ns:name"/>						<br/>						<xsl:value-of select="ns:public-office/ns:address/ns:street"/>						<br/>						<xsl:value-of select="ns:public-office/ns:address/ns:zipcode"/>						<br/>						<xsl:value-of select="ns:public-office/ns:address/ns:city"/>						<br/>						<xsl:value-of select="ns:public-office/ns:address/ns:country"/>						<br/>					</td>				</tr>				<tr>					<th>Oeffnungszeiten</th>				</tr>				<tr>					<td>						<xsl:for-each select="ns:public-office/ns:open">							<xsl:value-of select="."/>							<br/>						</xsl:for-each>					</td>				</tr>				<xsl:if test="ns:public-office/ns:location-image">					<tr>						<td>							<br/>							<img src="{ns:public-office/ns:location-image}" alt="Location Image"/>						</td>					</tr>				</xsl:if>				<xsl:if test="ns:accessroute">					<tr>						<td>							<br/>							<button onclick="top.location='{ns:accessroute}'">Anreise</button>						</td>					</tr>				</xsl:if>				<tr>					<th>						<hr/>					</th>				</tr>			</table>		</div>	</xsl:template>	<xsl:template match="ns:courses">		<table>			<tr align="left">				<td>					<b>						<xsl:choose>							<xsl:when test="ns:href">								<a href="{ns:href}">									<xsl:if test="ns:term/@season='WS'">Wintersemester </xsl:if>									<xsl:if test="ns:term/@season='SS'">Sommersemester </xsl:if>									<xsl:value-of select="ns:term/@year"/>								</a>							</xsl:when>							<xsl:otherwise>								<xsl:if test="ns:term/@season='WS'">Wintersemester </xsl:if>								<xsl:if test="ns:term/@season='SS'">Sommersemester </xsl:if>								<xsl:value-of select="ns:term/@year"/>							</xsl:otherwise>						</xsl:choose>					</b>				</td>			</tr>			<xsl:for-each select="ns:course">				<tr>					<td>						<xsl:choose>							<xsl:when test="@courseType='lecture'">Vorlesung: </xsl:when>							<xsl:when test="@courseType='practical'">Praktikum: </xsl:when>							<xsl:when test="@courseType='seminar'">Seminar: </xsl:when>						</xsl:choose>						<a href="{@href}">							<xsl:value-of select="."/>						</a>					</td>				</tr>			</xsl:for-each>			<br/>		</table>	</xsl:template>	<xsl:template match="ns:assignments">		<br/>		<xsl:value-of select="ns:description"/>		<xsl:text> </xsl:text>		<a href="{@href}">Liste</a>		<br/>		<br/>		<xsl:if test="ns:advices/ns:advice">Bitte beachten Sie die folgenden Hinweise:<br/>			<br/>			<ul>										<xsl:for-each select="ns:advices/ns:advice">				<li>zu <a href="{@href}">					<xsl:value-of select="."/>				</a></li>			</xsl:for-each></ul>		</xsl:if>	</xsl:template>	<xsl:template match="ns:assignment">	</xsl:template></xsl:stylesheet>