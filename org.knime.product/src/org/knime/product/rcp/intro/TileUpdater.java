/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Oct 7, 2019 (Daniel Bogenrieder): created
 */
package org.knime.product.rcp.intro;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import org.knime.core.node.NodeLogger;
import org.knime.product.rcp.intro.json.OfflineJsonCollector;

/**
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class TileUpdater extends AbstractUpdater {

    private final boolean m_isFreshWorkspace;

    /**
     * @param introPageFile the intro page file in the temporary directory
     * @param introFileLock lock for the intro file
     * @param isFreshWorkspace
     */
    protected TileUpdater(final File introPageFile, final ReentrantLock introFileLock, final boolean isFreshWorkspace) {
        super(introPageFile, introFileLock);
        m_isFreshWorkspace = isFreshWorkspace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareData() throws Exception {
        if (IntroPage.MOCK_INTRO_PAGE) {
            Thread.sleep(1500);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateData() {
        OfflineJsonCollector collector = new OfflineJsonCollector();
        String tiles = "";
        try {
            if (m_isFreshWorkspace) {
                hideElement("hub-search-bar");
                tiles = collector.fetchFirstUse();
            } else {
                tiles = collector.fetchAllOffline();
            }
            updateTiles(tiles);
        } catch (IOException e) {
            NodeLogger.getLogger(TileUpdater.class).error("Could not display tiles: " + e.getMessage(), e);
        }
    }

    private void hideElement(final String id) {
        executeUpdateInBrowser("hideElement('" + id + "');");
    }

    private void updateTiles(final String tiles) {
        executeUpdateInBrowser("updateTile(" + tiles + ");");
    }
}
