package ray_tracer;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

public class MyRecursiveTask extends RecursiveTask<int[]> {

	private long workLoad = 0;
	private int minw;
	private int maxw;
	private int minh;
	private int maxh;

	public MyRecursiveTask(long workLoad, int minw, int maxw, int minh, int maxh) {
		this.workLoad = workLoad;
		this.minw = minw;
		this.maxw = maxw;
		this.minh = minh;
		this.maxh = maxh;
	}

	protected int[] compute() {
		int[] result = new int[0];
		if (workLoad >= 2) {
			List<MyRecursiveTask> subtasks = new ArrayList<MyRecursiveTask>();
			subtasks.addAll(createSubTasks());

			for (MyRecursiveTask subtask : subtasks) {
				subtask.fork();
			}
			for (MyRecursiveTask subtask : subtasks) {
				result = join(result, subtask.join());
			}
			return result;
		} else {
			RayTracerV2 use = new RayTracerV2(minw, minh, maxw, maxh);
			result = use.getColors();
			return result;
		}
	}

	private List<MyRecursiveTask> createSubTasks() {
		List<MyRecursiveTask> subtasks = new ArrayList<MyRecursiveTask>();
//		MyRecursiveTask task1 = new MyRecursiveTask(this.workLoad / 4, minw, (minw + maxw) / 2, minh, (minh + maxh) / 2);
//		MyRecursiveTask task2 = new MyRecursiveTask(this.workLoad / 4, (minw + maxw) / 2, maxw, minh, (minh + maxh) / 2);
//		MyRecursiveTask task3 = new MyRecursiveTask(this.workLoad / 4, minw, (minw + maxw) / 2, (minh + maxh) / 2, maxh);
//		MyRecursiveTask task4 = new MyRecursiveTask(this.workLoad / 4, (minw + maxw) / 2, maxw, (minh + maxh) / 2, maxh);
		MyRecursiveTask task1 = new MyRecursiveTask(this.workLoad / 4, minw, (minw+maxw)/2, minh, maxh);
		MyRecursiveTask task2 = new MyRecursiveTask(this.workLoad / 4, (minw+maxw)/2, maxw, minh, maxh);
		subtasks.add(task1);
		subtasks.add(task2);
		return subtasks;
	}

	private int[] join(int[] i1, int[] i2) {
		int[] toReturn = Arrays.copyOf(i1, i1.length + i2.length);
		for (int i = i1.length; i < toReturn.length; i++) {
			toReturn[i] = i2[i - i1.length];
		}
		return toReturn;
	}

}
