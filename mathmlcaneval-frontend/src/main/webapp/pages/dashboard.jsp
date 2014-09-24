<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<tiles:insertDefinition name="default">
    <tiles:putAttribute name="body">
        <div class="container content">
            <h1><spring:message code="dashboard.title" /></h1>

            <div class="panel panel-primary">
                <div class="panel-heading"><spring:message code="dashboard.appStatus" /></div>
                <table class="table table-bordered table-striped">
                     <tr>
                           <td><spring:message code="dashboard.appStatus.heap" /></td>
                           <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${freeHeap}" /> MB /
                        <fmt:formatNumber type="number" maxFractionDigits="3" value="${maxHeap}" /> MB</td>
                    </tr>
                    <tr>
                        <td><spring:message code="dashboard.appStatus.cpu" /></td>
                        <td><c:out value="${cpuAverage}" /></td>
                    <tr>
                        <td><spring:message code="dashboard.appStatus.threads" /></td>
                        <td><c:out value="${threadCount}" /></td>
                    </tr>
                    <tr>
                        <td><spring:message code="dashboard.appStatus.uptime" /></td>
                        <td><c:out value="${uptime_h}" />:<c:out value="${uptime_m}" />:<c:out value="${uptime_s}" /></td>
                    </tr>
                </table>
            </div>

            <div class="panel panel-primary">
                <div class="panel-heading"><spring:message code="dashboard.task.running" /></div>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th><spring:message code="dashboard.task.start" /></th>
                            <th><spring:message code="dashboard.task.stop" /></th>
                            <th><spring:message code="dashboard.task.type" /></th>
                            <th><spring:message code="dashboard.task.note" /></th>
                            <th><spring:message code="dashboard.task.user" /></th>
                            <th><spring:message code="dashboard.task.progress" /></th>
                        </tr>
                    </thead>
                    <c:choose>
                        <c:when test="${fn:length(taskList) gt 0}">
                            <c:forEach items="${taskList}" var="task">
                                <tr>
                                    <td><joda:format value="${task.startTime}" style="SS" /></td>
                                    <td><joda:format value="${task.stopTime}" style="SS" /></td>
                                    <td><c:out value="${task.taskType}" /></td>
                                    <td><c:out value="${task.note}" /></td>
                                    <td><c:out value="${task.user.username}" /></td>
                                    <td><c:out value="${task.current}" /> / <c:out value="${task.total}" /></td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="text-center"><spring:message code="dashboard.task.noRunning" /></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </table>
            </div>
        </div>
    </tiles:putAttribute>
</tiles:insertDefinition>
