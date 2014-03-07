<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link type='text/css' rel='stylesheet' media='all' href='<c:url value="/comp/js/Calendario/calendar-blue2.css"/>' title='win2k-cold-1' />
<script type='text/javascript' src='<c:url value="/comp/js/Calendario/calendar.js"/>'></script>
<script type='text/javascript' src='<c:url value="/comp/js/Calendario/lang/calendar-es.js"/>'></script>
<script type='text/javascript' src='<c:url value="/comp/js/Calendario/calendar-setup.js"/>'></script>
<script>
var nav4=window.Event ? true : false;
function soloNumeros(eve){
	var key=nav4?eve.which :eve.keyCode;
	return(key<=13 || (key>=48 && key<=57));
}
function guardar(){

	 document.listadoRubros.submit();
}
function suma(formulario1){
	 for(var i=0;i<formulario1.idRubro.length;i++){
		document.listadoRubros.valorEntidad[i].value=eliminaFormatoMoneda(document.listadoRubros.valorEntidad[i].value);
	 }
	 document.listadoRubros.submit();
}
function enviar(id,action,idfinanciero){
	
	document.listadocdp.accion.value=action;
    document.listadocdp.idcdp.value=id;
	document.listadocdp.idcdpfinanciero.value=idfinanciero;
	document.listadocdp.submit();

}


</script>
<c:import url="/general.jsp"/>

</head>
<body>

<br>
	<table cellpadding="0" cellspacing="0">
		<tr>
			<td><a href='<c:url value="/adminConvenio/AdminConvenio.x?accion=3&idConv=${sessionScope.datoConvenio.idconvenio}"/>'><img border="0" src='<c:url value="/comp/img/convenio/Proyectos.gif"/>'></a></td>
		    <td><a href='<c:url value="/adminConvenio/AdminConvenio.x?accion=7"/>'><img border="0" src='<c:url value="/comp/img/convenio/Documentos.gif"/>'></a></td>
			<td><a href='<c:url value="/adminConvenio/VerTiempos.jsp"/>'><img border="0" src='<c:url value="/comp/img/convenio/Tiempos.gif"/>'></a></td>
			<td><a href='<c:url value="/adminConvenio/Personas.jsp"/>'><img border="0" src='<c:url value="/comp/img/convenio/Participantes.gif"/>'></a></td>
			<td><img border="0" src='<c:url value="/comp/img/convenio/GruposInvClick.gif"/>'></a></td>

		</tr>
	</table>
<br>
<c:if test="${!empty sessionScope.datoConvenio.listacdpsConv}">
<form action='<c:url value="/adminConvenio/AdminConvenio.x"/>' name="listadocdp">
<input type="hidden" name="accion" value="19">
<input type="hidden" name="idcdp" value="">
<input type="hidden" name="idcdpfinanciero" value="">

<table align="center" class="tablas">
<caption>Lista de CDPs</caption>
<c:set var="cantidade" value="${0}"></c:set>
	        <tr>
				<th style="width:400px;"><b>Rubro</b></th>
				<th><b>Codigo</b></th>
				<c:forEach begin="0" items="${sessionScope.datoConvenio.listaentidadesConv}" var="lis" varStatus="st">
				<th style="width:100px;"><b><c:out value="${lis.entidadid}"/></b></th>
				<c:set var="cantidade" value="${cantidade+1}"></c:set>
				</c:forEach>
				<th style="width:80px;" align="center"><b>valortotal</b></th>
				<th style="width:80px;" align="center"><b>fechaRegistro</b></th>
				
<th style="width:500px;" align="center"><b>observacion</b></th>
			</tr>
			
			
			<c:forEach begin="0" items="${sessionScope.datoConvenio.listacdpsConv}" var="lista" varStatus="st">
			<tr>
			<td style="width:400px;" align="right">
			  <b><input type="text"  name="nombre" readonly="readonly"   value='<c:out value="${lista.nombre}"/>'> </b>
		    </td>
		    <td style="width:80px;" align="right">
			  <b><input type="text"  name="codigo" readonly="readonly"  value='<c:out value="${lista.codigo}"/>'></b>
			</td>
			
			<%--  <c:forEach begin="0" items="${sessionScope.datoConvenio.listaentidadesConv}" var="lista" varStatus="st">
				<td style="width:100px;" align="right">
				    <input type="hidden" name="idconvent" value='<c:out value="${lista.identidadconvenio}"/>'>  
					<input id='ventidad<c:out value="${st.count}" />' maxlength="10" size="10" type="text" onkeypress="return soloNumeros(event)" name="valorEntidad" value='<c:out value="${sessionScope.datoConvenio.finanza.idfinanza}"/>'>
				</td>
			</c:forEach>
			--%>
			
			
			<c:forEach var="i" items="${lista.valores}" begin="0">
			 <td style="width:100px;" align="right">
		     <input type="text" name="valorEntidad"   readonly="readonly" value='<c:out value="${i}"/>'>
			</td>
			</c:forEach>
			
			
			<td style="width:80px;" align="right">
			  <b><input type="text"  name="valortotal"   readonly="readonly" value='<c:out value="${lista.valortotal}"/>'></b>
			</td>
			<td style="width:80px;" align="right">
			  <b><input type="text"  name="fechaRegistro"  readonly="readonly" value='<c:out value="${lista.fechaRegistro}"/>'></b>
			</td>
			<td style="width:500px;" align="right">
			  <b><input type="text"  name="observacion"  readonly="readonly" value='<c:out value="${lista.observacion}"/>'></b>
			</td>
			<td class="estado" align="center"><img src='<c:url value="/comp/img/Ver.gif"/>'  onclick='enviar("${lista.idcdp}",19,"${lista.financiero}")'></td>
			
			</tr>
			</c:forEach>
			   
</table>
</form>
<br/>
</c:if>

<c:if test="${empty sessionScope.datoConvenio.listacdpsConv}">
	<h3 align="center">No hay CDPS aprobados para este proyecto</h3>
</c:if>

<form action='<c:url value="/adminConvenio/AdminConvenio.x"/>' name="listadoRubros">
	<input type="hidden" name="accion" value="16">
    <input type="hidden" name="idfinanza" value='<c:out value="${sessionScope.datoConvenio.finanza.idfinanza}"/>'>
	<c:set var="var2" value="${0}"></c:set>
	<table align="center" class="tablas">
	<caption>Insertar CDP</caption>
		<c:if test="${!empty requestScope.listaRubros}">
			<tr>
				<th style="width:150px;"><b>Rubro</b></th>
				<th><b>Codigo</b></th>
				<c:forEach begin="0" items="${sessionScope.datoConvenio.listaentidadesConv}" var="lista" varStatus="st">
				<th style="width:100px;"><b><c:out value="${lista.entidadid}"/></b></th>
				<c:set var="var2" value="${var2+1}"></c:set>
				</c:forEach>
				<input type="hidden" name="numeroentidad" value="${var2}">
				<th style="width:400px;" align="center"><b>observacion</b></th>
			</tr>
			<tr>
			   <td> <select name="idRubro" style="width:150px" >
     		   <c:if test="${!empty requestScope.listaRubros}">
			   <c:forEach begin="0" items="${requestScope.listaRubros}" var="lista" varStatus="st">
		       <option value="${lista.codigo}"> <c:out value="${lista.nombre}" /> </option>
			   </c:forEach>
		       </c:if>   
               </select>
               </td>
			   <td style="width:80px;" align="right">
					<b><input  maxlength="10"  type="text"  name="codigo"></b>
			   </td>
			   <c:forEach begin="0" items="${sessionScope.datoConvenio.listaentidadesConv}" var="lista" varStatus="st">
				<td style="width:100px;" align="right">
				    <input type="hidden" name="idconvent" value='<c:out value="${lista.identidadconvenio}"/>'>  
					<input id='ventidad<c:out value="${st.count}" />' maxlength="10" size="10" type="text" onkeypress="return soloNumeros(event)" name="valorEntidad" value="0">
				</td>
				</c:forEach>
				
				<td style="width:400px;" align="right">
					<b><textarea class="texto" name="observacion" style="width: 99%"></textarea></b>
				</td>
				
				</tr>
			
			
			
		</c:if>
			<tr>
				<td colspan="3" align="center"><img src='<c:url value="/comp/img/Guardar.gif"/>'  onclick="guardar()"></td>
			</tr>
	</table>
</form>

</body>
<script>

</script>
</html>