/**
 * 
 */
package ru.flashsafe.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import ru.flashsafe.FileController;
import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.operation.OperationState;

/**
 * @author Alexander Krysin
 *
 */
public class FolderDownloadOperation {
	private FileController fileController;
	public String currentLocation;
	private List<FileOperation> fileUploadOperations = new ArrayList<>();
	private long total_bytes = 0;
	private FolderDownloadOperation fuo;
	private FileObject path;
	private File targetPath;

	/**
	 * 
	 */
	public FolderDownloadOperation(FileController fileController, FileObject path, File targetPath) {
		// TODO Auto-generated constructor stub
		this.fileController = fileController;
		this.fuo = this;
		this.path = path;
		this.targetPath = targetPath;
	}
	
	public void common() {
		Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
            	downloadDir(path, targetPath, false);
            	return null;
            }
        };
        new Thread(task).start();
        try {
        	Thread.sleep(500);
        } catch(InterruptedException e) {
        	e.printStackTrace();
        }
        ((MainSceneController) fileController).downloadDir(path, targetPath, fuo);
	}
	
	private void downloadDir(FileObject path, File targetPath, boolean inside) {
		if(!inside) {}
		new File(targetPath.getAbsolutePath() + "/" + path.getName()).mkdir();
		try {
			List<FileObject> childs = ((MainSceneController) fileController).fileManager.list(path.getAbsolutePath());
			int childs_count = childs.size();
			if(childs_count > 0) {
				for(FileObject child : childs) {
		            if(child.getType() == FileObjectType.FILE) {
		                Task<Void> task = new Task<Void>() {
		                    @Override
		                    protected Void call() throws Exception {
		                        try {
		                        	new File(targetPath.getAbsolutePath() + "/" + path.getName() + "/" + child.getName()).createNewFile();
		                            FileOperation downloadOperation = ((MainSceneController) fileController).fileManager.copy(child.getAbsolutePath(), targetPath.getAbsolutePath() + "/" + path.getName() + "/" + child.getName());
		                            fileUploadOperations.add(downloadOperation);
		                            total_bytes = total_bytes + downloadOperation.getTotalBytes();
		                        } catch (FileOperationException e) {
		                        	e.printStackTrace();
		                        }
		                        return null;
		                    }
		                };
		                new Thread(task).start();
		            } else {
		                downloadDir(child, new File(targetPath.getAbsolutePath() + "/" + path.getName()), true);
		            }
		        }
	
			}
		} catch(FileOperationException e) {
			e.printStackTrace();
		}
	}
	
	public long getTotalBytes() {
		return total_bytes;
	}
	
	public long getTotalProcessedBytes() {
		long processed_bytes = 0;
		for(FileOperation fileUploadOperation : fileUploadOperations) {
			processed_bytes = processed_bytes + fileUploadOperation.getProcessedBytes();
		}
		return processed_bytes;
	}
	
	public int getTotalProgress() {
		int count = fileUploadOperations.size();
		if(count == 0) return 100;
		int progress = 0;
		for(FileOperation fileUploadOperation : fileUploadOperations) {
			progress = progress + fileUploadOperation.getProgress();
		}
		return progress / count;
	}
	
	public OperationState getState() {
		OperationState state = OperationState.FINISHED;
		for(FileOperation operation : fileUploadOperations) {
			if(operation.getState() != OperationState.FINISHED) {
				state = OperationState.IN_PROGRESS;
			}
		}
		return state;
	}

}
