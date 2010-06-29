/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.dispatch;

import org.sigmah.shared.command.Command;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public interface DispatchEventSource {
    <T extends Command> void registerListener(Class<T> commandClass, DispatchListener<T> listener);

    <T extends Command> void registerProxy(Class<T> commandClass, CommandProxy<T> proxy);
}
