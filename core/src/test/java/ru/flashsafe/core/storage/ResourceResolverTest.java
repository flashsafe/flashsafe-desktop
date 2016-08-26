package ru.flashsafe.core.storage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ru.flashsafe.core.file.event.FileManagementEventHandlerProvider;
import ru.flashsafe.core.old.storage.FlashSafeStorageDirectory;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.old.storage.FlashSafeStorageIdBasedService;
import ru.flashsafe.core.old.storage.ResourceResolver;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import ru.flashsafe.core.storage.exception.ResourceResolverException;

public class ResourceResolverTest {

    private static final Map<Long, List<FlashSafeStorageFileObject>> STORAGE_TEST_STRUCTURE = new HashMap<>();

    static {
        FlashSafeStorageDirectory directory1 = new FlashSafeStorageDirectory();
        //directory1.setId(1);
        directory1.setName("Directory1");
        FlashSafeStorageDirectory directory2 = new FlashSafeStorageDirectory();
        //directory2.setId(2);
        directory2.setName("Directory2");
        STORAGE_TEST_STRUCTURE.put(0L, Arrays.asList(new FlashSafeStorageFileObject[] { directory1, directory2 }));

        FlashSafeStorageDirectory directory21 = new FlashSafeStorageDirectory();
        //directory21.setId(3);
        directory21.setName("SubDirectory21");
        STORAGE_TEST_STRUCTURE.put(2L, Collections.<FlashSafeStorageFileObject> singletonList(directory21));
    }

    private FlashSafeStorageIdBasedService storageService;

    private ResourceResolver resolver;

    @Before
    public void init() throws FlashSafeStorageException {
        storageService = mock(FlashSafeStorageIdBasedService.class);
        /*when(storageService.list(anyLong())).thenAnswer(new Answer<List<FlashSafeStorageFileObject>>() {

            @Override
            public List<FlashSafeStorageFileObject> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return STORAGE_TEST_STRUCTURE.get((Long) args[0]);
            }
        });*/
        resolver = new ResourceResolver(storageService, mock(FileManagementEventHandlerProvider.class));
    }

    @Test
    public void resolveResource() throws ResourceResolverException {
        FlashSafeStorageFileObject actualResource = resolver.resolveResource("Directory2/SubDirectory21");
        assertThat(actualResource.getName(), equalTo("SubDirectory21"));
    }

    @Test(expected = ResourceResolverException.class)
    public void resolveResource_fails_if_non_existent_resource_provided() throws ResourceResolverException {
        resolver.resolveResource("NonExistentDirectory");
    }
}
