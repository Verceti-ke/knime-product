/*
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2009
 * University of Konstanz, Germany
 * Chair for Bioinformatics and Information Mining (Prof. M. Berthold)
 * and KNIME GmbH, Konstanz, Germany
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * -------------------------------------------------------------------
 *
 * History
 *   28.08.2005 (Florian Georg): created
 */
package org.knime.product.rcp;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;

/**
 * This advisor is used for configuring the workbench window and creating the
 * action bar advisor.
 *
 * @author Florian Georg, University of Konstanz
 */
public class KNIMEApplicationWorkbenchWindowAdvisor extends
        WorkbenchWindowAdvisor {

    /**
     * Creates a new workbench window advisor for configuring a workbench window
     * via the given workbench window configurer.
     *
     * @param configurer an object for configuring the workbench window
     */
    public KNIMEApplicationWorkbenchWindowAdvisor(
            final IWorkbenchWindowConfigurer configurer) {
        super(configurer);

    }

    /**
     * Creates our <code>KNIMEActionBarAdvisor</code> that form the action
     * bars.
     *
     * @param configurer the action bar configurer for the window
     * @return the action bar advisor for the window
     *
     * @see KNIMEApplicationActionBarAdvisor
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor
     *      #createActionBarAdvisor
     *      (org.eclipse.ui.application.IActionBarConfigurer)
     */
    @Override
    public ActionBarAdvisor createActionBarAdvisor(
            final IActionBarConfigurer configurer) {
        return new KNIMEApplicationActionBarAdvisor(configurer);
    }

    /**
     * Configures the initial settings of the application window.
     */
    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

        // configurer.setInitialSize(new Point(1024, 768));

        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);

        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=36961
        // We want to use ResourceNavigator, so we have to introduce this
        // dependency to org.eclipse.ui.ide (otherwise we don't see our
        // Resources)
        WorkbenchAdapterBuilder.registerAdapters();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void postWindowOpen() {
        PlatformUI.getWorkbench().addWorkbenchListener(
                new IWorkbenchListener() {
            @Override
            public void postShutdown(final IWorkbench workbench) {
                // do nothing
            }
            @Override
            public boolean preShutdown(final IWorkbench workbench, 
                    final boolean forced) {
                // Remove consoles manually in time. Otherwise they are removed,
                // when the display is already disposed and this causes 
                // exceptions
                // this is a workaround for bug 
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=257970
                // reported here:
                // http://dev.eclipse.org/newslists/news.eclipse.platform.rcp/msg35729.html

                ConsolePlugin.getDefault().getConsoleManager().removeConsoles(
                        ConsolePlugin.getDefault().getConsoleManager()
                        .getConsoles());
                return true;
            }
        });
    }
    
}
