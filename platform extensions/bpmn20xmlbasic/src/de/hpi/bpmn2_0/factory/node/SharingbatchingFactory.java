package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.cloud_resource.Sharingbatching;
/**
 * The factory for storage
 * 
 * @author Ahmed Fathallah
 *
 */
@StencilId({"sharingbatching"})
public class SharingbatchingFactory extends AbstractShapeFactory {

	@Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		try {
			Sharingbatching sharingbatching = new Sharingbatching();
			sharingbatching.setId(shape.getResourceId());	
			sharingbatching.setName(shape.getProperty("name"));
			sharingbatching.setType(shape.getProperty("type"));
			sharingbatching.setConfigurable(shape.getProperty("configurable"));
			sharingbatching.setLogicaltype(shape.getProperty("logicaltype"));
			
			return sharingbatching;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
}
