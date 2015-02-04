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

package org.nukedbit;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchStore {
    private final Client client;

    public ElasticSearchStore(Client client){

        this.client = client;
    }

    public void Store(Post post) throws IOException, ExecutionException, InterruptedException {
        XContentBuilder contentBuilder = createContentBuilder(post);
        IndexRequest indexRequest = createIndexRequest(post, contentBuilder);
        UpdateRequest updateRequest = createUpdateRequest(post, contentBuilder, indexRequest);
        client.update(updateRequest).get();
    }

    private UpdateRequest createUpdateRequest(Post post, XContentBuilder contentBuilder, IndexRequest indexRequest) {
        return new UpdateRequest("posts","post",String.valueOf(post.getId()))
                    .doc(contentBuilder)
                    .upsert(indexRequest);
    }

    private IndexRequest createIndexRequest(Post post, XContentBuilder contentBuilder) {
        return new IndexRequest("posts", "post",String.valueOf(post.getId()))
                    .source(contentBuilder);
    }

    private XContentBuilder createContentBuilder(Post post) throws IOException {
        return jsonBuilder()
                    .startObject()
                    .field("content", post.getContent())
                    .field("modifiedAt", post.getModifiedAt())
                    .field("title", post.getTitle())
                    .field("imageUrl", post.getImageUrl())
                    .endObject();
    }
}
