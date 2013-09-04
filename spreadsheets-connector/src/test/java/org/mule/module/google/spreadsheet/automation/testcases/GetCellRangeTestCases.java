package org.mule.module.google.spreadsheet.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.google.spreadsheet.model.Row;

public class GetCellRangeTestCases extends GoogleSpreadsheetsTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (Map<String, Object>) context.getBean("getCellRange");

			String spreadsheet = (String) testObjects.get("spreadsheetTitle");
			createSpreadsheet(spreadsheet);
			
			String title = (String) testObjects.get("worksheetTitle");
			int rowCount = (Integer) testObjects.get("rowCount");
			int colCount = (Integer) testObjects.get("colCount");
			
			createWorksheet(spreadsheet, title, rowCount, colCount) ;
			
			setRowValues(spreadsheet, title, (List<Row>) testObjects.get("rowsRef"));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Category({RegressionTests.class})
	@Test
	public void testGetCellRange() {
		try {
			MessageProcessor flow = lookupFlowConstruct("get-cell-range");
			testObjects.put("worksheet", (String) testObjects.get("worksheetTitle"));
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			List<Row> res = (List<Row>) response.getMessage().getPayload();
			
			assertEquals(2, res.size());
			Row row = res.get(0);
			assertEquals(2, row.getCells().size());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			String spreadsheet = (String) testObjects.get("spreadsheetTitle");
			deleteSpreadsheet(spreadsheet);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
