package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.cloud_resource.Elasticity;
/**
 * The factory for storage
 * 
 * @author Ahmed Fathallah
 *
 */
@StencilId({"elasticity"})
public class ElasticityFactory extends AbstractShapeFactory {

	@Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		try {
			Elasticity elasticity = new Elasticity();
			elasticity.setId(shape.getResourceId());	
			elasticity.setName(shape.getProperty("name"));
			elasticity.setType(shape.getProperty("type"));
			elasticity.setConfigurable(shape.getProperty("configurable"));
			elasticity.setLogicaltype(shape.getProperty("logicaltype"));
			
			return elasticity;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
}
