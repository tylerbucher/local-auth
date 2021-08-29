/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 - Present, Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.localauth;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.bson.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class MongoDbModuleTest {

    private MongoDbModule mongoDbModule;
    private MongoClient mongoClient;
    private MongoServer server;
    private final Config config = Mockito.mock(Config.class);
    private static final String DB_NAME = "testdb";

    @Before
    public void setUp() {
        server = new MongoServer(new MemoryBackend());
        // bind on a random local port
        InetSocketAddress serverAddress = server.bind();
        final String uri = "mongodb://" + new ServerAddress(serverAddress).toString();
        mongoClient = MongoClients.create(uri);
        mongoClient.getDatabase(DB_NAME).getCollection("invites").insertOne(
                new Document("_id", "default@test.com")
                        .append("permissions", Arrays.asList(1, 2, 3, 4, 5))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("invites").insertOne(
                new Document("_id", "default2@test.com")
                        .append("permissions", Arrays.asList(1, 2, 3, 4, 5))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("invites").insertOne(
                new Document("_id", "default3@test.com")
                        .append("permissions", Arrays.asList(1, 2, 3, 4, 5))
        );

        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "default@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", true)
                        .append("permissions", Arrays.asList(1, 2, 3, 4, 5))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "default1@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", false)
                        .append("permissions", Arrays.asList(1, 2, 3, 4, 5))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "default3@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", true)
                        .append("permissions", Arrays.asList(6, 7, 8, 9, 10))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "default4@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", false)
                        .append("permissions", Arrays.asList(1, 2, 3, 4, 5))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "default5@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", true)
                        .append("permissions", Arrays.asList(0))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "default6@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", true)
                        .append("permissions", Arrays.asList(0))
        );
        mongoClient.getDatabase(DB_NAME).getCollection("users").insertOne(
                new Document("_id", "defaultlite@test.com")
                        .append("passwordHash", "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1")
                        .append("active", true)
                        .append("permissions", new ArrayList<>())
        );

        mongoClient.getDatabase(DB_NAME).getCollection("nodes").insertOne(
                new Document("_id", "testid")
                        .append("defaultText", "test")
        );
        mongoClient.getDatabase(DB_NAME).getCollection("nodes").insertOne(
                new Document("_id", "testytt")
                        .append("defaultText", "testytt")
        );

        mongoDbModule = new MongoDbModule(uri, DB_NAME);
    }

    @Test
    public void noInviteFound() {
        Assert.assertNull(mongoDbModule.getUserInviteModelByEmail("noinvite@test.com"));
    }

    @Test
    public void findDefaultInvite() {
        Assert.assertNotNull(mongoDbModule.getUserInviteModelByEmail("default@test.com"));
    }

    @Test
    public void findAllInvites() {
        Assert.assertTrue(mongoDbModule.getAllUserInviteModels().size() >= 2);
    }

    @Test
    public void createInvite() {
        Assert.assertTrue(mongoDbModule.createNewInvite("onetimeInvite@test.com", new ArrayList<>()));
        Assert.assertNotNull(mongoClient.getDatabase(DB_NAME).getCollection("invites").find(com.mongodb.client.model.Filters.eq("_id", "onetimeInvite@test.com")).first());
    }

    @Test
    public void createInvite_emptyEmail() {
        Assert.assertFalse(mongoDbModule.createNewInvite("", new ArrayList<>()));
        Assert.assertNull(mongoClient.getDatabase(DB_NAME).getCollection("invites").find(com.mongodb.client.model.Filters.eq("_id", "")).first());
    }

    @Test
    public void noUserFound() {
        Assert.assertNull(mongoDbModule.getUserModelByEmail("nouser@test.com"));
    }

    @Test
    public void findDefaultUser() {
        Assert.assertNotNull(mongoDbModule.getUserModelByEmail("default@test.com"));
    }

    @Test
    public void createNewUserWithInvite() {
        Mockito.when(config.getAccountCreationType()).thenReturn(Config.ACCOUNT_CREATION_INVITE);
        Assert.assertTrue(mongoDbModule.createNewUser(config, "default2@test.com", "test"));
        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default2@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getList("permissions", Integer.class).containsAll(Arrays.asList(1, 2, 3, 4, 5)));
        Assert.assertTrue(doc.getBoolean("active"));
    }

    @Test
    public void createNewFirstUser() {
        final MongoServer tServer = new MongoServer(new MemoryBackend());
        // bind on a random local port
        InetSocketAddress serverAddress = server.bind();
        final String uri = "mongodb://" + new ServerAddress(serverAddress).toString();
        final MongoDbModule tMongoDbModule = new MongoDbModule(uri, "testdb2");
        final MongoClient tMongoClient = MongoClients.create(uri);
        ;

        Mockito.when(config.getAccountCreationType()).thenReturn(Config.ACCOUNT_CREATION_OPEN);
        Assert.assertTrue(tMongoDbModule.createNewUser(config, "test@test.com", "test"));
        final Document doc = tMongoClient.getDatabase("testdb2").getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "test@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getList("permissions", Integer.class).contains(Permissions.IS_USER_SUPER_ADMIN.value));
        Assert.assertTrue(doc.getBoolean("active"));

        tMongoClient.close();
        tMongoDbModule.close();
        tServer.shutdown();
    }

    @Test
    public void createNewUserWithInvite_declined() {
        Mockito.when(config.getAccountCreationType()).thenReturn(Config.ACCOUNT_CREATION_INVITE);
        Assert.assertFalse(mongoDbModule.createNewUser(config, "test3@test.com", "test"));
        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "test3@test.com")).first();
        Assert.assertNull(doc);
    }

    @Test
    public void createNewUserOpen_notActive() {
        Mockito.when(config.getAccountCreationType()).thenReturn(Config.ACCOUNT_CREATION_OPEN);
        Assert.assertTrue(mongoDbModule.createNewUser(config, "test4@test.com", "test"));
        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "test4@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertFalse(doc.getBoolean("active"));
    }

    @Test
    public void updateInvite_fail() {
        Assert.assertFalse(mongoDbModule.updateInvite("defaul@test.com", new ArrayList<>()));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("invites").find(com.mongodb.client.model.Filters.eq("_id", "default@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getList("permissions", Integer.class).containsAll(Arrays.asList(1, 2, 3, 4, 5)));
    }

    @Test
    public void updateInvite_success() {
        Assert.assertTrue(mongoDbModule.updateInvite("default@test.com", Arrays.asList(6, 7, 8, 9, 10)));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("invites").find(com.mongodb.client.model.Filters.eq("_id", "default@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getList("permissions", Integer.class).containsAll(Arrays.asList(6, 7, 8, 9, 10)));
    }

    @Test
    public void deleteInvite_success() {
        Assert.assertTrue(mongoDbModule.deleteInvite("default@test.com"));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("invites").find(com.mongodb.client.model.Filters.eq("_id", "default@test.com")).first();
        Assert.assertNull(doc);
    }

    @Test
    public void deleteInvite_fail() {
        Assert.assertFalse(mongoDbModule.deleteInvite("defaul@test.com"));
    }

    @Test
    public void findAllUsers() {
        Assert.assertTrue(mongoDbModule.getAllUserModels().size() >= 2);
    }

    @Test
    public void updateUser_fail() {
        Assert.assertFalse(mongoDbModule.updateUserModel("defaul@test.com", "", false, new ArrayList<>()));
    }

    @Test
    public void updateUser_setActive() {
        Assert.assertTrue(mongoDbModule.updateUserModel("default@test.com", "", false, new ArrayList<>()));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertFalse(doc.getBoolean("active"));
    }

    @Test
    public void updateUser_setPassword() {
        Assert.assertTrue(mongoDbModule.updateUserModel("default1@test.com", "123456", false, new ArrayList<>()));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default1@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertNotEquals(doc.getString("passwordHash"), "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1");
    }

    @Test
    public void updateUser_setPassword_lite() {
        Assert.assertTrue(mongoDbModule.updateUserModel("defaultlite@test.com", "123456", true, null));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "defaultlite@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertNotEquals(doc.getString("passwordHash"), "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1");
    }

    @Test
    public void updateUser_setPermissions() {
        Assert.assertTrue(mongoDbModule.updateUserModel("default3@test.com", "123456", false, Arrays.asList(6, 7, 8, 9, 10)));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default3@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getList("permissions", Integer.class).containsAll(Arrays.asList(6, 7, 8, 9, 10)));
    }

    @Test
    public void updateUser_setMultiple() {
        Assert.assertTrue(mongoDbModule.updateUserModel("default4@test.com", "123456", true, Arrays.asList(6, 7, 8, 9, 10)));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default4@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getBoolean("active"));
        Assert.assertNotEquals(doc.getString("passwordHash"), "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1");
        Assert.assertTrue(doc.getList("permissions", Integer.class).containsAll(Arrays.asList(6, 7, 8, 9, 10)));
    }

    @Test
    public void updateUser_failOnAdmin() {
        Assert.assertTrue(mongoDbModule.updateUserModel("default5@test.com", "123456", false, Arrays.asList(6, 7, 8, 9, 10)));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default5@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertTrue(doc.getBoolean("active"));
        Assert.assertNotEquals(doc.getString("passwordHash"), "1$2123$asd5165fd1gb65fg1h561sd5f16sa5d1");
        Assert.assertFalse(doc.getList("permissions", Integer.class).containsAll(Arrays.asList(6, 7, 8, 9, 10)));
    }

    @Test
    public void deleteUser_fail() {
        Assert.assertFalse(mongoDbModule.deleteUser("defaul@test.com"));
    }

    @Test
    public void deleteUser_successes() {
        Assert.assertTrue(mongoDbModule.deleteUser("default6@test.com"));
        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default6@test.com")).first();
        Assert.assertNull(doc);
    }

    @Test
    public void createNode_success() {
        Assert.assertTrue(mongoDbModule.createNewNode("test", "dd test"));
        Assert.assertNotNull(mongoClient.getDatabase(DB_NAME).getCollection("nodes").find(com.mongodb.client.model.Filters.eq("_id", "test")).first());
    }

    @Test
    public void createNode_fail() {
        Assert.assertFalse(mongoDbModule.createNewNode("", ""));
        Assert.assertNull(mongoClient.getDatabase(DB_NAME).getCollection("nodes").find(com.mongodb.client.model.Filters.eq("_id", "")).first());
    }

    @Test
    public void updateNode_fail() {
        Assert.assertFalse(mongoDbModule.updateNode("noidtest", ""));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("nodes").find(com.mongodb.client.model.Filters.eq("_id", "testid")).first();
        Assert.assertNotNull(doc);
        Assert.assertEquals("test", doc.getString("defaultText"));
    }

    @Test
    public void updateNode_success() {
        Assert.assertTrue(mongoDbModule.updateNode("testid", "new text"));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("nodes").find(com.mongodb.client.model.Filters.eq("_id", "testid")).first();
        Assert.assertNotNull(doc);
        Assert.assertEquals("new text", doc.getString("defaultText"));
    }

    @Test
    public void deleteNode_fail() {
        Assert.assertFalse(mongoDbModule.deleteNode("testytest"));
    }

    @Test
    public void deleteNode_successes() {
        Assert.assertTrue(mongoDbModule.deleteNode("testytt"));
        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("nodes").find(com.mongodb.client.model.Filters.eq("_id", "testytt")).first();
        Assert.assertNull(doc);
    }

    @Test
    public void findAllNodes() {
        Assert.assertTrue(mongoDbModule.getAllNodeModels().size() >= 1);
    }

    @Test
    public void updateUser_metadata_success() {
        Assert.assertTrue(mongoDbModule.updateUserNodeMetadata("default@test.com", "new text"));

        final Document doc = mongoClient.getDatabase(DB_NAME).getCollection("users").find(com.mongodb.client.model.Filters.eq("_id", "default@test.com")).first();
        Assert.assertNotNull(doc);
        Assert.assertEquals("new text", doc.getString("nodeMetaData"));
    }

    @After
    public void tearDown() {
        mongoClient.close();
        mongoDbModule.close();
        server.shutdown();
    }

}