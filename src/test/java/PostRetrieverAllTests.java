/*
 *     Import wordpress data to elastic search
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

import org.junit.Test;
import org.nukedbit.Post;
import org.nukedbit.PostRetriever;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PostRetrieverAllTests {

    @Test
    public void PrepareStatement() throws SQLException, ParseException {
        Connection connection = getConnection();
        final String getAllByDateQuery = getAllQueryString();
        PreparedStatement mockedStatement = getPreparedStatement(connection, getAllByDateQuery);
        PostRetriever retriever = new PostRetriever(connection);
        final Date startDate = getDate();
        retriever.all(startDate);
        verifyPrepareStatment(connection, getAllByDateQuery);
        verifySetDate(mockedStatement, startDate);
    }

    @Test
    public void ReturnPost() throws SQLException, ParseException {
        final Date startDate = getDate();
        Post expectedPost = getPost(startDate);
        Connection connection = getConnection();
        PreparedStatement mockedStatement = getPreparedStatement(connection, startDate);
        ResultSet resultSetMock = getResultSetMock(expectedPost, mockedStatement);
        PostRetriever retriever = new PostRetriever(connection);
        Post post = retriever.all(startDate).get(0);
        verifyExecuteAndResultSet(mockedStatement, resultSetMock);
        assertPost(expectedPost, post);
    }

    private void verifySetDate(PreparedStatement mockedStatement, Date startDate) throws SQLException {
        verify(mockedStatement, times(1)).setDate(1, new java.sql.Date(startDate.getTime()));
    }

    private void verifyPrepareStatment(Connection connection, String getAllByDateQuery) throws SQLException {
        verify(connection, times(1)).prepareStatement(getAllByDateQuery);
    }

    private void verifyExecuteAndResultSet(PreparedStatement mockedStatement, ResultSet resultSetMock) throws SQLException {
        verify(mockedStatement, times(1)).execute();
        verify(resultSetMock, times(2)).next();
    }

    private void assertPost(Post expectedPost, Post post) {
        assertEquals(expectedPost.getId(), post.getId());
        assertEquals(expectedPost.getModifiedAt(), post.getModifiedAt());
        assertEquals(expectedPost.getContent(), post.getContent());
        assertEquals(expectedPost.getTitle(), post.getTitle());
        assertEquals(expectedPost.getImageUrl(), post.getImageUrl());
    }

    private Post getPost(Date startDate) {
        return new Post(
                    1,
                    startDate,
                    "content",
                    "title",
                    "image_url"
            );
    }

    private PreparedStatement getPreparedStatement(Connection connection, Date startDate) throws SQLException {
        PreparedStatement mockedStatement = getPreparedStatement(connection, getAllQueryString());
        doNothing().when(mockedStatement).setDate(1, new java.sql.Date(startDate.getTime()));
        return mockedStatement;
    }

    private ResultSet getResultSetMock(Post expectedPost, PreparedStatement mockedStatement) throws SQLException {
        ResultSet resultSetMock = mock(ResultSet.class);
        when(mockedStatement.getResultSet()).thenReturn(resultSetMock);
        when(mockedStatement.execute()).thenReturn(true);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);

        when(resultSetMock.getInt("ID")).thenReturn(expectedPost.getId());
        when(resultSetMock.getDate("ModifiedAt")).thenReturn(new java.sql.Date(expectedPost.getModifiedAt().getTime()));
        when(resultSetMock.getString("Content")).thenReturn(expectedPost.getContent());
        when(resultSetMock.getString("Title")).thenReturn(expectedPost.getTitle());
        when(resultSetMock.getString("image_url")).thenReturn(expectedPost.getImageUrl());
        return resultSetMock;
    }


    private String getAllQueryString() {
        return "select p.ID, p.post_modified_gmt as ModifiedAt, p.post_content as Content, p.post_title as Title,pms.image_url from wp_posts p" +
                " inner join " +
                " ( SELECT jp.guid as image_url, pm.post_id, pm.meta_key, jp.post_date" +
                " FROM wp_postmeta AS pm" +
                " INNER JOIN wp_posts AS jp ON pm.meta_value=jp.ID" +
                " ORDER BY jp.post_date DESC" +
                ") pms on pms.post_id = p.id and  pms.meta_key = '_thumbnail_id'" +
                "     where p.post_type='post' and p.post_modified >= ?";
    }

    private PreparedStatement getPreparedStatement(Connection connection, String getAllByDateQuery) throws SQLException {
        PreparedStatement mockedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(getAllByDateQuery)).thenReturn(mockedStatement);
        return mockedStatement;
    }

    private Connection getConnection() throws SQLException {
        Connection mockedConnection = mock(Connection.class);
        return mockedConnection;
    }

    private Date getDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.parse("18-01-2015");
    }
}