import numpy as np
import scipy as sci 
import random
import matplotlib.pyplot as plt
import math
from sklearn.cluster import KMeans
import matplotlib.cm as cm
from scipy.spatial import distance as dist
from matplotlib.font_manager import FontProperties

def generate_circular_dataset(points_count, center_point=(0, 0), radius=1, min_inner_radius=0):
    dataset = []
    full_angle = 2 * math.pi
    radius_range = radius - min_inner_radius

    for _ in range(points_count):
        inner_radius = (random.random() * radius_range) + min_inner_radius
        phi = random.random() * full_angle

        x = center_point[0] + inner_radius * math.cos(phi)
        y = center_point[1] - inner_radius * math.sin(phi)
        dataset.append((x, y))
    
    return np.array(dataset)

def generate_samples(samples_per_cluster, rows, cols, radius=1, margin=1, first_point=(0, 0)):
    cur_center = list(first_point)
    step = 2 * radius + margin
    dataset = []

    for _ in range(rows):
        cur_center[0] = first_point[0]
        cur_center[1] += step

        for _ in range(cols):   
            dataset.extend(generate_circular_dataset(samples_per_cluster, cur_center))
            cur_center[0] += step
    
    return np.array(dataset)

def random_partition(dataset, k):
    permuted_dataset = np.random.permutation(dataset)
    full_clusters_count = k if (len(dataset) // k == 0) else k - 1
    samples_per_cluster = len(dataset) // k
    clusters = []

    for i in range(full_clusters_count):
        clusters.append(permuted_dataset[i * samples_per_cluster:(i + 1) * samples_per_cluster])
        
    if (full_clusters_count < k):
        clusters.append(permuted_dataset[(k - 1) * samples_per_cluster:])

    clusters = np.array(clusters)
    return (clusters, find_centroids(clusters))

def get_random_centroids(dataset, k):
    max_vals = np.amax(dataset, axis=0)
    min_vals = np.amin(dataset, axis=0)
    centroids = []

    for _ in range(k):
        centroid = [0] * len(max_vals)
        for dim in range(len(max_vals)):
            centroid[dim] = np.random.uniform(low=min_vals[dim], high=max_vals[dim])  
        centroids.append(centroid)

    return np.array(centroids)

def find_centroids(clusters):
    centroids = []

    for cluster in clusters:
        centroids.append(np.mean(cluster, axis=0))
    
    return np.array(centroids)

"""
In this script the Dunn index is expressed as the following ratio:

min distance between centroids
------------------------------------------------------------------
max average distance between a centroid and samples in its cluster

"""

def dunn_index(dataset, centroids, labels):
    min_inter_centroid_dist = np.amin(dist.pdist(centroids, 'euclidean'))
    average_dists = []

    for cluster_idx in range(len(centroids)):
        cluster = [dataset[i] for i in range(len(dataset)) if labels[i] == cluster_idx]

        if (len(cluster) > 0):
            average_point = centroids[cluster_idx]
            average_dists.append(
                np.average(dist.cdist(np.array([average_point]), cluster))    
        )
    
    max_average_dist_from_center = np.amax(average_dists)
    return (min_inter_centroid_dist / max_average_dist_from_center)

def plot_results(results, errors, color, name, figure=plt):
    figure.scatter(np.arange(len(results)), results ,color=color, label=name)
    font_props = FontProperties()
    font_props.set_size('small')
    figure.legend(prop = font_props)
    figure.errorbar(np.arange(len(results)), results, yerr=errors, color=color, fmt="o")

def scatter_samples(dataset, chart_color, figure=plt):
    figure.scatter(dataset[:, 0], dataset[:, 1], color=chart_color)

"""
An excerpt from the scikit.cluster documentation concerning the
initialization methods available in the KMeans class:

init : {‘k-means++’, ‘random’ or an ndarray}
Method for initialization, defaults to ‘k-means++’:
‘k-means++’ : selects initial cluster centers for k-mean clustering in a smart way to speed up convergence. 
    See section Notes in k_init for more details.
‘random’: choose k observations (rows) at random from data for the initial centroids.
If an ndarray is passed, it should be of shape (n_clusters, n_features) and gives the initial centers.

It seems that the 'random' method is an equivalent of the Forgy method from the description of the task A.
"""

if __name__ == "__main__":
    k = 9
    test_count = 100
    max_iters = 30
    samples_per_cluster = 100

    methods = ["random", "forgy", "random_partition", "k-means++"]

    cluster_quality_for_method = {}
    for method in methods:
        cluster_quality_for_method[method] = [[] for _ in range(max_iters)]
    
    color_map = cm.rainbow(np.linspace(0, 1, k))    
    plt.rc('font', family='Noto Mono')
    plt.figure(figsize=(10, 7), dpi=100)
    plt.axis("equal")
    plt.legend()
    dataset = generate_samples(samples_per_cluster, 3, 3, radius=1, margin=1)

    for _ in range(test_count):
        for init_method in methods:
            centroids = []

            if (init_method == "random"):
                centroids = get_random_centroids(dataset, k)
            elif (init_method == "forgy"):
                centroids = "random"
            elif (init_method == "random_partition"):
                _, centroids = random_partition(dataset, k)
            else:
                centroids = init_method
            
            for iteration in range(max_iters):
                kmeans = KMeans(n_clusters=k, init=centroids, n_init=1, max_iter=1).fit(dataset)
                centroids = kmeans.cluster_centers_
                cluster_quality_for_method[init_method][iteration].append(
                    dunn_index(dataset, centroids, kmeans.labels_))
                
                # colors = [color_map[category] for category in kmeans.labels_]
                # scatter_samples(centroids, "black")
                # scatter_samples(dataset, colors)
                # plt.show()
                # plt.clf()


    avg_cluster_quality_for_method = {}
    std_cluster_quality_for_method = {}

    color_for_method = {
        methods[0]: "c",
        methods[1]: "r",
        methods[2]: "g",
        methods[3]: "b"
    }

    for init_method in methods:
        avg_cluster_quality_for_method[init_method] = [[] for _ in range(max_iters)]
        std_cluster_quality_for_method[init_method] = [[] for _ in range(max_iters)]

        for iteration in range(max_iters):
            avg_cluster_quality_for_method[init_method][iteration] = np.average(cluster_quality_for_method[init_method][iteration])
            std_cluster_quality_for_method[init_method][iteration] = np.std(cluster_quality_for_method[init_method][iteration])

        plot_results(
            avg_cluster_quality_for_method[init_method],
            std_cluster_quality_for_method[init_method], 
            name=init_method,
            color=color_for_method[init_method])

    plt.title("Średnia jakość klasteryzacji dla danej iteracji (liczba testów: " + str(test_count) + ")")
    plt.xlabel("nr iteracji")
    plt.ylabel("Dunn index")
    plt.savefig("cluster_quality_" + str(test_count) + ".png")