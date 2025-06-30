if __name__ == '__main__':
    import geopandas as gpd

    # Load the GeoJSON file into a GeoDataFrame
    input_geojson = "features_2000.geojson"
    gdf = gpd.read_file(input_geojson)

    batches = [gdf.iloc[i:i + 500] for i in range(0, len(gdf), 500)]

    for idx, batch in enumerate(batches):
        output_file = f"batch_{idx + 1}.geojson"
        batch.to_file(output_file, driver="GeoJSON")
   


