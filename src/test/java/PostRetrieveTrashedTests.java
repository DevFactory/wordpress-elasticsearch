/*
 * Import wordpress data to elastic search
 *
 *     Copyright (C) 2015  Sebastian Faltoni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import junit.framework.Assert;
import org.junit.Test;
import org.nukedbit.PostRetriever;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class PostRetrieveTrashedTests {

    @Test
    public void RetrieveAllPostsTrashed() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement stmt = getPreparedStatement(connection);
        Integer expectedId = 123;
        ResultSet resultSet = getResultSetMock(expectedId, stmt);
        PostRetriever retriever = new PostRetriever(connection);
        List<Integer> ids = retriever.allTrashed();
        verify(stmt, times(1)).execute();
        verify(stmt, times(1)).getResultSet();
        verify(resultSet, times(2)).next();
        assertEquals(1, ids.size());
        assertEquals(expectedId, ids.get(0));
    }

    private ResultSet getResultSetMock(Integer expectedId, PreparedStatement mockedStatement) throws SQLException {
        ResultSet resultSetMock = mock(ResultSet.class);
        when(mockedStatement.getResultSet()).thenReturn(resultSetMock);
        when(mockedStatement.execute()).thenReturn(true);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);

        when(resultSetMock.getInt("ID")).thenReturn(expectedId);
        return resultSetMock;
    }

    private String getQueryString() {
        return "SELECT ID FROM wp_posts where post_status='trashed'";
    }

    private PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement mockedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(getQueryString())).thenReturn(mockedStatement);
        return mockedStatement;
    }

    private Connection getConnection() throws SQLException {
        Connection mockedConnection = mock(Connection.class);
        return mockedConnection;
    }
}
