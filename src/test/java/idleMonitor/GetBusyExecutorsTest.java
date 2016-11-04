package idleMonitor;

import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import dataUtils.ParsingUtils;

public class GetBusyExecutorsTest {
	
	ParsingUtils parsing = new ParsingUtils();
	
	@Test
	public void testZero() {		
		
		String str = "{'assignedLabels':[{'actions':[],'busyExecutors':0,'clouds':[],'description':null,'idleExecutors':2,'loadStatistics':{},'name':'master','nodes':[{'nodeName':''}],'offline':false,'tiedJobs':[],'totalExecutors':2,'propertiesList':[]}],'mode':'NORMAL','nodeDescription':'the master Jenkins node','nodeName':'','numExecutors':2,'description':null,'jobs':[{'actions':[{},{}],'description':'','displayName':'test','displayNameOrNull':null,'name':'test','url':'http://localhost:8080/job/test/','buildable':false,'builds':[],'color':'notbuilt','firstBuild':null,'healthReport':[],'inQueue':false,'keepDependencies':false,'lastBuild':null,'lastCompletedBuild':null,'lastFailedBuild':null,'lastStableBuild':null,'lastSuccessfulBuild':null,'lastUnstableBuild':null,'lastUnsuccessfulBuild':null,'nextBuildNumber':1,'property':[],'queueItem':null}],'overallLoad':{'availableExecutors':{},'busyExecutors':{},'connectingExecutors':{},'definedExecutors':{},'idleExecutors':{},'onlineExecutors':{},'queueLength':{},'totalExecutors':{},'totalQueueLength':{}},'primaryView':{'description':null,'jobs':[{'name':'test','url':'http://localhost:8080/job/test/','color':'notbuilt'}],'name':'All','property':[],'url':'http://localhost:8080/'},'quietingDown':false,'slaveAgentPort':-1,'unlabeledLoad':{'availableExecutors':{},'busyExecutors':{},'connectingExecutors':{},'definedExecutors':{},'idleExecutors':{},'onlineExecutors':{},'queueLength':{},'totalExecutors':{}},'useCrumbs':true,'useSecurity':true,'views':[{'description':null,'jobs':[{'name':'test','url':'http://localhost:8080/job/test/','color':'notbuilt'}],'name':'All','property':[],'url':'http://localhost:8080/'}]}";				
				
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject input = (JSONObject) parser.parse(str);
			
			long output = parsing.parseJenkinsData(input);
			
			assertEquals(0, output);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testPosInt() {		
		
		String str = "{'assignedLabels':[{'actions':[],'busyExecutors':6,'clouds':[],'description':null,'idleExecutors':2,'loadStatistics':{},'name':'master','nodes':[{'nodeName':''}],'offline':false,'tiedJobs':[],'totalExecutors':2,'propertiesList':[]}],'mode':'NORMAL','nodeDescription':'the master Jenkins node','nodeName':'','numExecutors':2,'description':null,'jobs':[{'actions':[{},{}],'description':'','displayName':'test','displayNameOrNull':null,'name':'test','url':'http://localhost:8080/job/test/','buildable':false,'builds':[],'color':'notbuilt','firstBuild':null,'healthReport':[],'inQueue':false,'keepDependencies':false,'lastBuild':null,'lastCompletedBuild':null,'lastFailedBuild':null,'lastStableBuild':null,'lastSuccessfulBuild':null,'lastUnstableBuild':null,'lastUnsuccessfulBuild':null,'nextBuildNumber':1,'property':[],'queueItem':null}],'overallLoad':{'availableExecutors':{},'busyExecutors':{},'connectingExecutors':{},'definedExecutors':{},'idleExecutors':{},'onlineExecutors':{},'queueLength':{},'totalExecutors':{},'totalQueueLength':{}},'primaryView':{'description':null,'jobs':[{'name':'test','url':'http://localhost:8080/job/test/','color':'notbuilt'}],'name':'All','property':[],'url':'http://localhost:8080/'},'quietingDown':false,'slaveAgentPort':-1,'unlabeledLoad':{'availableExecutors':{},'busyExecutors':{},'connectingExecutors':{},'definedExecutors':{},'idleExecutors':{},'onlineExecutors':{},'queueLength':{},'totalExecutors':{}},'useCrumbs':true,'useSecurity':true,'views':[{'description':null,'jobs':[{'name':'test','url':'http://localhost:8080/job/test/','color':'notbuilt'}],'name':'All','property':[],'url':'http://localhost:8080/'}]}";				
				
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject input = (JSONObject) parser.parse(str);
			
			long output = parsing.parseJenkinsData(input);
			
			assertEquals(6, output);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	

}
