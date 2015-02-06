
/*
 *     Import wordpress data to elastic search
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

package org.nukedbit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostRetriever {
    private final Connection connection;

    public PostRetriever(Connection connection) {

        this.connection = connection;
    }

    private final String getAllByDateQuery = "select p.ID, p.post_modified_gmt as ModifiedAt, p.post_content as Content, p.post_title as Title,pms.image_url from wp_posts p" +
            " inner join " +
            " ( SELECT jp.guid as image_url, pm.post_id, pm.meta_key, jp.post_date" +
            " FROM wp_postmeta AS pm" +
            " INNER JOIN wp_posts AS jp ON pm.meta_value=jp.ID" +
            " ORDER BY jp.post_date DESC" +
            ") pms on pms.post_id = p.id and  pms.meta_key = '_thumbnail_id'" +
            "     where p.post_type='post' and p.post_modified >= ?";


    private final String getTrashedPosts = "SELECT ID FROM wp_posts where post_status='trashed'";

    public List<Post> all(Date startDate) throws SQLException {
        PreparedStatement stmt = this.connection.prepareStatement(getAllByDateQuery);
        stmt.setDate(1,new java.sql.Date(startDate.getTime()));
        if(!stmt.execute())
            return new ArrayList<Post>();
        final ResultSet postResultSet = stmt.getResultSet();
        ArrayList<Post> posts = new ArrayList<Post>();
        while(postResultSet.next()){
            posts.add(new Post(postResultSet.getInt("ID"),
                    postResultSet.getDate("ModifiedAt"),
                    postResultSet.getString("Content"),
                    postResultSet.getString("Title"),
                    postResultSet.getString("image_url")
            ));
        }
        return posts;
    }

    public List<Integer> allTrashed() throws SQLException {
        PreparedStatement stmt = this.connection.prepareStatement(getTrashedPosts);
        if(!stmt.execute())
        {
            return new ArrayList<Integer>();
        }
        final ResultSet postResultSet = stmt.getResultSet();
        ArrayList<Integer> postIds = new ArrayList<Integer>();
        while(postResultSet.next()) {
            postIds.add(postResultSet.getInt("ID"));
        }
        return postIds;
    }
}
