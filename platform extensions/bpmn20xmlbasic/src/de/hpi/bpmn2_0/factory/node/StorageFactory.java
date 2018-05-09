package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.cloud_resource.Storage;
/**
 * The factory for storage
 * 
 * @author Karn Yongsiriwit
 *
 */
@StencilId({"Storage"})
public class StorageFactory extends AbstractShapeFactory {

	@Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		try {
			Storage storage = new Storage();
			storage.setId(shape.getResourceId());	
			storage.setName(shape.getProperty("name"));
			String size = shape.getProperty("size");
			storage.setSize(size);
			String sstate = shape.getProperty("sstate");
			storage.setSState(sstate);
						
			//StorageList.getInstance().storages.put(storage.getId(), storage);
			
			return storage;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
}
