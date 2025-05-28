import React, { createContext, useContext, useEffect, useState } from 'react';
import { TagService } from '../service/TagService';

interface Tag {
    id: string;
    name: string;
}

interface TagContextType {
    tags: Tag[];
    fetchTags: () => Promise<void>;
    setTags: (tags: Tag[]) => void;
}

const TagContext = createContext<TagContextType | undefined>(undefined);

export const TagProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [tags, setTags] = useState<Tag[]>([]);
    
    useEffect(() => {
        fetchTags();
        console.log("tags feteched");
    }, []);

    const fetchTags = async () => {
        try {
            const fetchedTags = await TagService.fetchTags();
            setTags(fetchedTags);
        } catch (error) {
            console.error('Failed to fetch tags:', error);
        }
    };

    const setTagsHandler = (newTags: Tag[]) => {
        setTags(newTags.filter((tag) => tag.name.trim()));
    };

    return (
        <TagContext.Provider value={{ tags, fetchTags, setTags: setTagsHandler }}>
            {children}
        </TagContext.Provider>
    );
};

export const useTags = () => {
    const context = useContext(TagContext);
    if (!context) throw new Error('useTags must be used within a TagProvider');
    return context;
};