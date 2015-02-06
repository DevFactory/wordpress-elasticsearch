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


import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.nukedbit.ElasticSearchStore;
import org.nukedbit.Post;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class ElasticSearchStoreTests {

    @Test
    public void StorePosts() throws ParseException, InterruptedException, ExecutionException, IOException {
        Client clientMock = mock(Client.class);
        UpdateResponse updateResponse = mock(UpdateResponse.class);
        ActionFuture<UpdateResponse> responseFuture = createActionFuture();
        when(responseFuture.get()).thenReturn(updateResponse);
        when(clientMock.update((UpdateRequest) anyObject())).thenReturn(responseFuture);
        ElasticSearchStore store = new ElasticSearchStore(clientMock);
        Post post = createPost();
        store.Store(post);
        verify(clientMock,times(1)).update((UpdateRequest) anyObject());
    }

    private ActionFuture<UpdateResponse> createActionFuture() {
        return (ActionFuture<UpdateResponse>) mock(ActionFuture.class);
    }

    private Post createPost() throws ParseException {
        return new Post(1, getDate(), "content", "title", "image_url");
    }

    private Date getDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.parse("18-01-2015");
    }
}
