/**
 *  интерфейс для рассылки команд по подписке
 *  
 *  */
package org.oakimsoft.common;

import java.awt.event.ActionEvent;

/**
 * @author Ruslan.Strelnikov
 * 
 */
public interface ActionObserver {
	public void subscribedAction(ActionEvent ae);
}
