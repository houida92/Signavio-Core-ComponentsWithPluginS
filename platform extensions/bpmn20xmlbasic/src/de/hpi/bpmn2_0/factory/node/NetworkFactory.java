package de.hpi.bpmn2_0.factory.node;

import javax.xml.namespace.QName;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.cloud_resource.Network;
/**
 * The factory for network
 * 
 * @author Souha Boubaker
 *
 */
@StencilId({"network"})
public class NetworkFactory extends AbstractShapeFactory {

	@Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		try {
			Network network = new Network();
			network.setId(shape.getResourceId());	
			network.setName(shape.getProperty("name"));
			String capacity = shape.getProperty("capacity");
			network.setCapacity(capacity);
			String nstate = shape.getProperty("nstate");
			network.setNState(nstate);
						
				
			return network;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
}
