package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.cloud_resource.Assignment;
/**
 * The factory for storage
 * 
 * @author Ahmed Fathallah
 *
 */
@StencilId({"assignment"})
public class AssignmentFactory extends AbstractShapeFactory {

	@Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		try {
			Assignment assignment = new Assignment();
			assignment.setId(shape.getResourceId());	
			assignment.setName(shape.getProperty("name"));
			assignment.setType(shape.getProperty("type"));
			assignment.setConfigurable(shape.getProperty("configurable"));
			
			int rangecomputemin = 0;
			if(!shape.getProperty("rangecomputemin").equals("")){
				rangecomputemin =  Integer.parseInt(shape.getProperty("rangecomputemin"));
				
			}
			assignment.setRangecomputemin(rangecomputemin);
			
			int rangecomputemax = 0;
			if(!shape.getProperty("rangecomputemax").equals("")){
				rangecomputemax =  Integer.parseInt(shape.getProperty("rangecomputemax"));
				
			}
			assignment.setRangecomputemax(rangecomputemax);
			
			int rangestoragemin = 0;
			if(!shape.getProperty("rangestoragemin").equals("")){
				rangestoragemin =  Integer.parseInt(shape.getProperty("rangestoragemin"));
				
			}
			assignment.setRangestoragemin(rangestoragemin);
			
			int rangestoragemax = 0;
			if(!shape.getProperty("rangestoragemax").equals("")){
				rangestoragemax =  Integer.parseInt(shape.getProperty("rangestoragemax"));
				
			}
			assignment.setRangestoragemax(rangestoragemax);
			
			int rangenetworkmin = 0;
			if(!shape.getProperty("rangenetworkmin").equals("")){
				rangenetworkmin =  Integer.parseInt(shape.getProperty("rangenetworkmin"));
				
			}
			assignment.setRangenetworkmin(rangenetworkmin);
			
			int rangenetworkmax = 0;
			if(!shape.getProperty("rangenetworkmax").equals("")){
				rangenetworkmax =  Integer.parseInt(shape.getProperty("rangenetworkmax"));
				
			}
			assignment.setRangenetworkmax(rangenetworkmax);
			
			return assignment;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
}
