/**
 * This is the skeleton code for bfs.
 * Note the save and load functions in each struct and class are used to serialize
 * and deserialize the object. If you add members with non-primitive data types in
 * a struct or class, you need to implement the save and load functions. For more
 * information, please see the serialization section in the API documentation of
 * GraphLab.
 */

#include <vector>
#include <string>
#include <fstream>

#include <graphlab.hpp>

const int MAX_LEN = 15;

/**
 * The vertex data type and also the message type
 */
struct vertex_data {

  // add data members here
  std::vector<int> path;
  bool visited=false;
  // GraphLab will use this function to merge messages 
  vertex_data& operator+=(const vertex_data& other) {
        // Fill your code here
		if(this->path.size()<other.path.size() && this->path.size()!=0)
		{
        
		
		}
		else if ((this->path.size()==other.path.size())&& this->path.size()!=0)
		{
			for(int i=other.path.size();i>=0; i--){
						if(other.path[i]< this->path[i]){
						this->path=other.path;
									}
		}}
		else
		{
				this->path=other.path;
				
		}
		return *this;
	}
  
  void save(graphlab::oarchive& oarc) const {
        // Fill your code here
		oarc << path;
  }

  void load(graphlab::iarchive& iarc) {
        // Fill your code here
		iarc >> path;
  }
};

/**
 * Definition of graph
 */
typedef graphlab::distributed_graph<vertex_data, graphlab::empty> graph_type;

/* The current id we are processing */
int id;

/**
 * The bfs program.
 */
class bfs :
  public graphlab::ivertex_program<graph_type, 
                                   graphlab::empty,
                                   vertex_data>  {
private:
    vertex_data msg;
	bool changed;
public:

  void init(icontext_type& context, const vertex_type& vertex,
            const message_type& msg) {
        this->msg = msg;
		this->msg.path.push_back(vertex.id());
        // Fill your code here
		
  }		

  // no gather required
  edge_dir_type gather_edges(icontext_type& context,
                             const vertex_type& vertex) const {
        return graphlab::NO_EDGES;
  }


  void apply(icontext_type& context, vertex_type& vertex,
             const gather_type& gather_result) {
//      dc.cout<<"the id is"<< vertex.id();
	  changed = false;
	  // Fill your code here
	 // if(vertex.data().path.size()>=this->msg.path.size()){
//	 msg.path.push_back(vertex.id());
	vertex.data() = this->msg;
			changed = true;  }

  // do scatter on all the in-edges
  edge_dir_type scatter_edges(icontext_type& context,
                             const vertex_type& vertex) const {
        return graphlab::IN_EDGES;
  }

  void scatter(icontext_type& context, const vertex_type& vertex,
               edge_type& edge) const {
        // Fill your code here
		if((edge.source().data().path.size()==0))
		{
		//edge.source().data().visited = true;
			context.signal(edge.source(), msg);
		}
	
  }

  void save(graphlab::oarchive& oarc) const {
        // Fill your code here
		oarc << msg;
  }

  void load(graphlab::iarchive& iarc) {
        // Fill your code here
		iarc >> msg;
  }
};

void initialize_vertex(graph_type::vertex_type& vertex) {
    // Fill your code here
    // You should initialize the vertex data here
	vertex.data().path.clear();
//	vertex.data().visited=false;
}

struct shortest_path_writer {
  std::string save_vertex(const graph_type::vertex_type& vtx) {
    // You should print the shortest path here
	std::stringstream strm;
	int len = vtx.data().path.size()-1;
	if(len>0) {
	strm << vtx.id() << "\t"<< vtx.data().path[0] << "\t";
	for(int i=vtx.data().path.size()-1;i>=0;i--)
	{
		if(i==0){
		strm<< vtx.data().path[i];}
		if(i>0)
		{
			
			strm<< vtx.data().path[i]<<" ";
		}
	}
		strm<< "\n";}
		return strm.str();
  }

  std::string save_edge(graph_type::edge_type e) { return ""; }
};

int main(int argc, char** argv) {
  // Initialize control plain using mpi
  graphlab::mpi_tools::init(argc, argv);
  graphlab::distributed_control dc;
  global_logger().set_log_level(LOG_INFO);

  // Parse command line options -----------------------------------------------
  graphlab::command_line_options
    clopts("bfs algorithm");
  std::string graph_dir;
  std::string format = "snap";
  std::string saveprefix;
  std::string top_ids;

  clopts.attach_option("graph", graph_dir,
                       "The graph file.");
  clopts.attach_option("format", format,
                       "graph format");
  clopts.attach_option("top", top_ids,
                       "The file which contains top 10 ids");
  clopts.attach_option("saveprefix", saveprefix,
                       "If set, will save the result to a "
                       "sequence of files with prefix saveprefix");

  if(!clopts.parse(argc, argv)) {
    dc.cout() << "Error in parsing command line arguments." << std::endl;
    return EXIT_FAILURE;
  }


  // Build the graph
  graph_type graph(dc, clopts);

  dc.cout() << "Loading graph in format: "<< format << std::endl;
  graph.load_format(graph_dir, format);

  // must call finalize before querying the graph
  graph.finalize();
  dc.cout() << "#vertices:  " << graph.num_vertices() << std::endl
            << "#edges:     " << graph.num_edges() << std::endl;

  graphlab::synchronous_engine<bfs> engine(dc, graph, clopts);
  char id_str[MAX_LEN];
  std::ifstream fin(top_ids.c_str());
  while (fin >> id) {
    graph.transform_vertices(initialize_vertex);
	//dc.cout()<<"the id is:"<<id;
	engine.signal(id);
engine.start();	
    /*
     * add your implementation here
     */

    std::string tmp = saveprefix;
    tmp += '_';
    sprintf(id_str, "%d", id);
    tmp += id_str;
    graph.save(tmp,
            shortest_path_writer(),
            false,   // do not gzip
            true,    // save vertices
            false,   // do not save edges
            1);      // one output file per machine
  }
  fin.close();

  // Tear-down communication layer and quit
  graphlab::mpi_tools::finalize();
  return EXIT_SUCCESS;
} // End of main
